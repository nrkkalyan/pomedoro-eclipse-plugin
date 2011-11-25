/* Copyright 2010 The Rabbit Eclipse Plug-in Project
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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.IKey;
import rabbit.data.access.model.Key;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @see KeyMapBuilder
 */
public class KeyMapBuilderTest {
  
  /**
   * Helper class containing sample keys for testing.
   */
  private static class Keys {
    static final IKey<Integer> COUNT = Key.create();
    static final IKey<String> ID = Key.create();
  }
  
  private KeyMapBuilder builder;
  
  @Before
  public void prepare() {
    builder = new KeyMapBuilder();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void theMapBuiltShouldBeImmutable() {
    builder.build().put(Keys.COUNT, 0);
  }
  
  @Test
  public void shouldBuildMapCorrectly() {
    Map<IKey<? extends Object>, Object> map = 
        builder.put(Keys.COUNT, 1).put(Keys.ID, "abc").build();
    assertThat(map.get(Keys.COUNT), is((Object) 1));
    assertThat(map.get(Keys.ID), is((Object) "abc"));
    assertThat(map.size(), is(2));
  }
  
  @Test
  public void shouldReturnItselfAfterEachPutOperation() {
    assertThat(builder.put(Keys.COUNT, 1), is(sameInstance(builder)));
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenPuttingANullKey() {
    builder.put(null, 1);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenPuttingANullValue() {
    builder.put(Keys.COUNT, null);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenPuttingANullKeyAndANullValue() {
    builder.put(null, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionWhenBuildingWhenDuplicateKeys() {
    builder.put(Keys.COUNT, 1).put(Keys.COUNT, 0).build();
  }
}
