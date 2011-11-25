/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.internal.viewers;

import com.google.common.base.Joiner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Base test class for forwarding test classes.
 */
public abstract class ForwardingTestCase {

  /**
   * A list containing each method call as a string using
   * {@link #callToString(String, Object...)}.
   */
  private List<String> calls = new ArrayList<String>();

  @Before
  public void clearMethodCalls() {
    calls.clear();
  }

  /**
   * Asserts that only the given method with the given arguments is called on
   * the current proxy.
   * @param methodName The name of the method.
   * @param args The arguments passed to the method.
   */
  protected void assertCall(String methodName, @Nullable Object... args) {
    assertThat(methodsCalled(), is(callToString(methodName, args)));
  }

  /**
   * Converts a method call to string.
   * @param methodName the name of the method.
   * @param args the arguments passed to the invocation, nullable.
   * @return a string representation of this method invocation.
   */
  protected String callToString(String methodName, @Nullable Object... args) {
    if (args == null) {
      args = new Object[0];
    }
    return new StringBuilder(methodName)
        .append("(")
        .append(Joiner.on(", ").useForNull("null").join(args))
        .append(")")
        .toString();
  }

  /**
   * Gets a string representation of all the method calls. Each call is built
   * using {@link #callToString(String, Object...)} and then joined together
   * using a ', '
   */
  protected String methodsCalled() {
    return Joiner.on(", ").join(calls);
  }

  /**
   * Creates a new instance of the given interface. When any method of the proxy
   * is called, the call will be recorded, then {@link #methodsCalled()} will
   * return the method call.
   * @param c the instance type.
   * @return an instance.
   */
  protected <T> T newProxy(Class<T> c) {
    ClassLoader loader = c.getClassLoader();
    Class<?>[] types = {c};
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
        calls.add(callToString(method.getName(), args));
        
        if (method.getReturnType() == boolean.class) {
          return Boolean.FALSE;
        }
        return null;
      }
    };

    @SuppressWarnings("unchecked")
    T instance = (T) Proxy.newProxyInstance(loader, types, handler);
    return instance;
  }
}
