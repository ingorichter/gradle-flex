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

class FlexPluginConvention {
	
	Project project
	String output
    
    // the home directory of the Flex SDK
    def flexHome
    
    //default location to look for flex libraries
    String thirdPartyLibsDir = '../flexLibraries/lib'
    
    // the directory where we should publish the build artifacts
    String publishDir
    
    // which directories to look into for source code
	def srcDirs = []
    
    // which directories to look in for assets to be added build (images, fonts, ...)
	def assets = []
	
    // what type of Flex project are we?  either SWF or SWC
    FlexType type
    
    // location of the Flex services file to compile against
    def serviceFile
    
    // Java web application context root
    def contextRoot
    
    // type selectors - default to keeping them all, even those considered unused
    def keepAllTypeSelectors = true
    
    // the Flex platform libraries
    def platformLibs
    
	FlexPluginConvention(Project project) {
		this.project = project
        
        // no point adding a source directory if the project doesn't contain it
		addSourceDirectoriesIfTheyExist(project, ['src', 'skin', 'test', 'assets'])
        
        // default output directory for the build artifacts
		project.buildDir = project.file('bin-gradle')
        
		addConfigurations(project)
	}
	
    
    // standard configurations supported by the Flex plugin
	private void addConfigurations(Project project) {
		project.configurations.add('rsl')
		project.configurations.add('external')
		project.configurations.add('merge')
		project.configurations.add('libraries')
	}
	
	private void addSourceDirectoriesIfTheyExist(Project project, srcDirs) {
		srcDirs.each { srcDir ->
			if(project.file("${srcDir}").exists()) {
                this.srcDirs.add(srcDir)
			}
		}
	}
}