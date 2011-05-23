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

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

class Mxmlc extends DefaultTask {

	
	@TaskAction
	def compileFlex() {
		ant.mxmlc(output: project.output, 
                  file:   project.target, 
                  fork:   'true', 
                  'keep-all-type-selectors': project.keepAllTypeSelectors,
                  'verify-digests': 'false',
                  debug: 'true') {

			// source paths (relative to project directory)
			
			project.srcDirs.each { dir ->
				println "adding mxmlc source path: ${dir}"
				'source-path'('path-element': dir)
			}
		
			// platform dependencies
								
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/textLayout.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/tlf/1.1.0.604/textLayout_1.1.0.604.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/framework.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/framework_4.1.0.16076.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/osmf.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/osmf_flex.4.0.0.13495.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/rpc.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/rpc_4.1.0.16076.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/spark.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/spark_4.1.0.16076.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/sparkskins.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/sparkskins_4.1.0.16076.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
			
			'runtime-shared-library-path'('path-element': '${FLEX_LIB}/datavisualization.swc') {
				url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/datavisualization_4.1.0.16076.swz',
				'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
			}
						
			'library-path'(file: "${FLEX_LIB}/flash-integration.swc", append: 'true')
			'library-path'(file: "${FLEX_LIB}/utilities.swc", append: 'true')
			'library-path'(file: "${FLEX_HOME}/frameworks/locale/en_US", append: 'true')

			// project-specific dependencies
			
			addLibraries('external-library-path', project.files(project.configurations.external))
			addLibraries('library-path', project.files(project.configurations.merge))
			addRsls(project.files(project.configurations.rsl))
		}
	}
	
	def compileFlexBroken() {
		if(project.serviceFile == null || project.contextRoot == null ) {
			mxmlcWithoutServices {
				mxmlcBody
			}
		}
		else {
			mxmlcWithServices {
				mxmlcBody
			}
		}
	}

	def mxmlcWithoutServices(closure) {
		ant.mxmlc(output: project.output, 
                  file:   project.target, 
                  fork:   'true', 
                  'keep-all-type-selectors': project.keepAllTypeSelectors,
                  'verify-digests': 'false',
                  debug: 'true') {
			closure()
		}
	}
	
	def mxmlcWithServices(closure) {
		ant.mxmlc(output: project.output, 
                  file:   project.target, 
                  fork:   'true', 
                  services: project.serviceFile,
                  'context-root': project.contextRoot,
                  'keep-all-type-selectors': project.keepAllTypeSelectors,
                  'verify-digests': 'false',
                  debug: 'true') {
			closure()
		}
	}
	
	def mxmlcBody = { ->
		// source paths (relative to project directory)
			
		project.srcDirs.each { dir ->
			println "adding mxmlc source path: ${dir}"
			'source-path'('path-element': dir)
		}
	
		// platform dependencies
							
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/textLayout.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/tlf/1.1.0.604/textLayout_1.1.0.604.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/framework.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/framework_4.1.0.16076.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/osmf.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/osmf_flex.4.0.0.13495.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/rpc.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/rpc_4.1.0.16076.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/spark.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/spark_4.1.0.16076.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/sparkskins.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/sparkskins_4.1.0.16076.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
		
		'runtime-shared-library-path'('path-element': '${FLEX_LIB}/datavisualization.swc') {
			url('rsl-url': 'http://fpdownload.adobe.com/pub/swz/flex/4.1.0.16076/datavisualization_4.1.0.16076.swz',
			'policy-file-url': 'http://fpdownload.adobe.com/pub/swz/crossdomain.xml')
		}
					
		'library-path'(file: "${FLEX_LIB}/flash-integration.swc", append: 'true')
		'library-path'(file: "${FLEX_LIB}/utilities.swc", append: 'true')
		'library-path'(file: "${FLEX_HOME}/frameworks/locale/en_US", append: 'true')

		// project-specific dependencies
				
		addLibraries('external-library-path', project.files(project.configurations.external))
		addLibraries('library-path', project.files(project.configurations.merge))
		addRsls(project.files(project.configurations.rsl))
	}
	
	def addLibraries(String libraryPath, FileCollection libraries) {
		libraries?.files.each { library ->
			if(library.exists()) {
				println "adding dependency ${library.name}"
				ant."${libraryPath}"(file: library.absolutePath, append: 'true')
			}
			else {
				throw new ResolveException("Couldn't find the ${library.name} file - are you sure the path is correct?")
			}
		}
	}
	
	def addRsls(FileCollection libraries) {
		libraries?.files.each { library ->
			if(library.exists()) {
				println "adding rsl ${library.name}"
				ant.'runtime-shared-library-path'('path-element': library.path) {
					url('rsl-url': library.name[0..-2] + 'f')
				}
			}
			else {
				throw new ResolveException("Couldn't find the ${library.name} file - are you sure the path is correct?")
			}
				
		}
	}
}