/*
 * Copyright 2011 Infonic AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashSet
import java.util.Set

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.tasks.Delete

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FlexPlugin implements Plugin<Project> {

	public static final String COMPILE_TASK_NAME = 'compileFlex'
    public static final String PUBLISH_TASK_NAME = 'publishFlex'
	public static final String CLEAN_TASK_NAME = 'clean'
	public static final String BUILD_TASK_NAME = 'build'
	
	Logger log = LoggerFactory.getLogger('flex')
	
	public void apply(Project project) {
		configureAnt(project)
	
		FlexPluginConvention pluginConvention = new FlexPluginConvention(project)
		project.convention.plugins.flex = pluginConvention

		identifyProjectType(project)
		configureBuild(project)
		addCompile(project, pluginConvention)
		
		addClean(project)
        addPublish(project)
        
		addDependsOnOtherProjects(project)
		addDefaultArtifact(project)
        
        project.platformLibs = getPlatformLibs(project)
	}
	
	private void identifyProjectType(Project project) {
		if(project.file("${project.projectDir}/.flexLibProperties").exists()) {
			project.type = FlexType.swc
		} else if(project.file("${project.projectDir}/.flexProperties").exists()) {
			project.type = FlexType.swf
		} else {
			throw new IllegalStateException('Cannot determine if the the project is an SWC or SWF.')
		}
	}
	
	private void configureAnt(Project project) {
		project.flexHome = System.env['FLEX_HOME']
		if(project.flexHome == null) {
			throw new IllegalStateException('You need to set the FLEX_HOME environment variable to use the Flex Gradle plugin.')
		}
		project.ant.property(name: 'FLEX_HOME',       value: project.flexHome)
		project.ant.property(name: 'FLEX_LIB',        value: '${FLEX_HOME}/frameworks/libs')
		project.ant.property(name: 'FLEX_ANT',        value: '${FLEX_HOME}/ant')
		project.ant.property(name: 'FLEX_ANTLIB',     value: '${FLEX_ANT}/lib')
        project.ant.property(name: 'FLEX_PLAYER_LIB', value: '${FLEX_LIB}/player/10.0')
	
		project.ant.taskdef(resource: 'flexTasks.tasks') {
		    classpath {
		        fileset(dir: '${FLEX_ANTLIB}') {
		        	include(name: 'flexTasks.jar')
		        }
		    }
		}	
	}
	
	private void configureBuild(Project project) {
		DefaultTask buildTask = project.tasks.add(BUILD_TASK_NAME, DefaultTask)
		buildTask.setDescription("Assembles and tests this project.")
        buildTask.dependsOn(COMPILE_TASK_NAME)
	}
	
	private void addCompile(Project project, FlexPluginConvention pluginConvention) {
		Task compileFlex = null
		if(project.type == FlexType.swc) {
			log.info "Adding ${COMPILE_TASK_NAME} task using compc to project ${project.name}" 
			compileFlex = project.tasks.add(COMPILE_TASK_NAME, Compc)
			compileFlex.description = 'Compiles Flex component (*.swc) using the compc compiler'
			compileFlex.outputs.dir project.buildDir
			pluginConvention.output = "${project.buildDir}/${project.name}.swc"
		}
		else if(project.type == FlexType.swf) {
			log.info "Adding ${COMPILE_TASK_NAME} task using mxmlc to project ${project.name}" 
			compileFlex = project.tasks.add(COMPILE_TASK_NAME, Mxmlc)
			compileFlex.description = 'Compiles Flex application/module (*.swf) using the mxmlc compiler'
			pluginConvention.output = "${project.buildDir}/${project.name}.swf"
			project.target = "${project.projectDir}/src/${project.name}.mxml"
		}
		else {
			log.warn "Adding ${COMPILE_TASK_NAME} task using default implementation"
			compileFlex = project.tasks.add(COMPILE_TASK_NAME, DefaultTask)
			compileFlex.description = "Oops - we couldn't figure out if ${project.name} is a Flex component or a Flex application/module project."
		}
		compileFlex.dependsOn(CLEAN_TASK_NAME)
	}
	
	private void addClean(final Project project) {
		Delete clean = project.tasks.add(CLEAN_TASK_NAME, Delete)
		clean.description = "Deletes the build directory."
		clean.delete { project.buildDir }
	}
	
	private void addDependsOnOtherProjects(Project project) {
		// dependencies need to be added as a closure as we don't have the information at the moment to wire them up
		project.tasks.compileFlex.dependsOn {
			Set dependentTasks = new HashSet()
			['external', 'merge', 'rsl'].each { configuration ->
				Set deps = project.configurations."${configuration}".getDependencies(ProjectDependency)
				println "deps are: ${deps}"
		    	deps.each { projectDependency ->
					//def projectDependency = (ProjectDependency) dependency
					println "path to dependency: ${projectDependency.dependencyProject.path}"
					dependentTasks.add(projectDependency.dependencyProject.path + ':compileFlex')
				}
			}
			dependentTasks
		}
	}
	
	private void addDefaultArtifact(Project project) {
		project.artifacts {
			libraries new DefaultPublishArtifact(project.name, project.type.toString(), project.type.toString(), null, new Date(), new File(project.output))
		}
	}
    
    private void addPublish(Project project) {
        Task publishFlex = project.tasks.add(PUBLISH_TASK_NAME, PublishFlex)
        publishFlex.setDescription("Publish build artifacts to specified directory.")
    }
    
    private def getPlatformLibs(Project project) {
        project.fileTree(dir: "${project.flexHome}/frameworks/rsls", exclude: '*.swz')
    }
}