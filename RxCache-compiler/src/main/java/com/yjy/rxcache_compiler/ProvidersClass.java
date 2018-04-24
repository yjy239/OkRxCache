/*
 * Copyright 2016 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yjy.rxcache_compiler;

import com.squareup.javapoet.ClassName;
import com.sun.tools.javac.code.Type;

import java.util.List;

import javax.lang.model.element.Element;

final class ProvidersClass {
    final ClassName className;
    final Element element;
    final List<Method> methods;

    ProvidersClass(ClassName className, Element element,
                   List<Method> methods) {
        this.className = className;
        this.element = element;
        this.methods = methods;
    }

    static class Method {
        final String name;
        final Element element;
        final Type enclosingTypeObservable;


        Method(String name, Element element, Type enclosingTypeObservable) {
            this.name = name;
            this.element = element;
            this.enclosingTypeObservable = enclosingTypeObservable;
        }

    }
}
