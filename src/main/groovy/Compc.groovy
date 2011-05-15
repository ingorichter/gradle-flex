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


class Compc extends DefaultTask {

	@TaskAction
	def compileFlex() {
		ant.compc(output: project.output, fork: 'true', debug: 'true') {
		
			// source paths (relative to project directory)
			
			project.srcDirs.each { dir ->
				println "adding path-element: ${dir}"
				'source-path'('path-element': "${dir}")
				println "including AS3 sources: ${dir}"
				'include-sources'(dir: "${dir}", includes: '**/*.as **/*.mxml')
			}
	
			// assets (such as graphics files)
			
			project.assets.each { asset ->
				println "including assets: ${asset}"
				'include-file'(name: asset, path: file("${project.projectDir}/assets/${asset}"))
			}
	
			// Flex platform dependencies
	
	    	'external-library-path'(dir: '${FLEX_LIB}') {
	    		println 'configuring external library path'
				include(name: 'flex.swc')
				include(name: 'textLayout.swc')
				include(name: 'osmf.swc')
				include(name: 'framework.swc')
				include(name: 'spark.swc')
				include(name: 'sparkskins.swc')
	            include(name: 'rpc.swc')
	            include(name: 'datavisualization.swc')
	            include(name: 'flash-integration.swc')
	            include(name: 'utilities.swc')
			}
			
			'external-library-path'(file: '${FLEX_PLAYER_LIB}/playerglobal.swc', append: 'true')
			
			// project-specific dependencies
			
			addLibraries('external-library-path', project.files(project.configurations.external))
			addLibraries('library-path',          project.files(project.configurations.merge))
			
		}
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
	
}