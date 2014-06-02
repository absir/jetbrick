/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 * Email: subchen@gmail.com
 * URL: http://subchen.github.io/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.reflect;

import java.util.List;
import jetbrick.beans.ClassUtils;

final class ExecutableUtils {

    /**
     * 查找完全匹配的方法或者构造函数
     */
    public static <T extends Executable> T searchExecutable(List<T> executables, String name, Class<?>... parameterTypes) {
        for (T info : executables) {
            if (info.getName().equals(name)) {
                Class<?>[] types = info.getParameterTypes();
                if (parameterTypes.length == types.length) {
                    boolean match = true;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (types[i] != parameterTypes[i]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查找最佳匹配的方法或者构造函数
     */
    public static Executable searchBestExecutable(Class<?> declaringClass, List<? extends Executable> executables, String name, Class<?>... parameterTypes) {
        Executable best = null;
        Class<?>[] bestParametersTypes = null;

        for (Executable execute : executables) {
            if (!execute.getName().equals(name)) continue;

            Class<?>[] types = execute.getParameterTypes();
            if (isParameterTypesCompatible(types, parameterTypes, execute.isVarArgs(), false)) {
                // 可能有多个方法与实际参数类型兼容。采用就近兼容原则。
                if (best == null) {
                    best = execute;
                    bestParametersTypes = types;
                } else if (best.isVarArgs() && (!execute.isVarArgs())) {
                    best = execute; // 不可变参数的函数优先
                    bestParametersTypes = types;
                } else if ((!best.isVarArgs()) && execute.isVarArgs()) {
                    // no change
                } else {
                    if (isParameterTypesCompatible(bestParametersTypes, types, best.isVarArgs(), execute.isVarArgs())) {
                        best = execute;
                        bestParametersTypes = types;
                    }
                }
            }
        }
        return best;
    }

    /**
     * 判断参数列表是否兼容, 支持可变参数
     */
    public static boolean isParameterTypesCompatible(Class<?>[] lhs, Class<?>[] rhs, boolean lhsVarArgs, boolean rhsVarArgs) {
        if (lhs == null) {
            return rhs == null || rhs.length == 0;
        }
        if (rhs == null) {
            return lhs.length == 0;
        }

        if (lhsVarArgs && rhsVarArgs) {
            if (lhs.length != rhs.length) {
                return false;
            }
            //校验前面的固定参数
            for (int i = 0; i < lhs.length - 1; i++) {
                if (!ClassUtils.isAssignable(lhs[i], rhs[i])) {
                    return false;
                }
            }
            // 校验最后一个可变参数
            Class<?> c1 = lhs[lhs.length - 1].getComponentType();
            Class<?> c2 = rhs[rhs.length - 1].getComponentType();
            if (!ClassUtils.isAssignable(c1, c2)) {
                return false;
            }
        } else if (lhsVarArgs) {
            if (lhs.length - 1 > rhs.length) {
                return false;
            }
            //校验前面的固定参数
            for (int i = 0; i < lhs.length - 1; i++) {
                if (!ClassUtils.isAssignable(lhs[i], rhs[i])) {
                    return false;
                }
            }
            // 校验最后一个可变参数
            Class<?> varType = lhs[lhs.length - 1].getComponentType();
            for (int i = lhs.length - 1; i < rhs.length; i++) {
                if (!ClassUtils.isAssignable(varType, rhs[i])) {
                    return false;
                }
            }
        } else {
            if (lhs.length != rhs.length) {
                return false;
            }
            for (int i = 0; i < lhs.length; i++) {
                if (!ClassUtils.isAssignable(lhs[i], rhs[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
