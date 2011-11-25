/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.merge.AbstractMerger;

import org.junit.Test;

/**
 * @see AbstractMerger
 */
public abstract class AbstractMergerTest<T> {

  protected AbstractMerger<T> merger = createMerger();

  /**
   * Tests {@link AbstractMerger#isMergeable(Object, Object)} returns what's
   * desire for the subclass implementation.
   */
  @Test
  public abstract void testIsMergeable() throws Exception;

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_bothParamNull() {
    merger.isMergeable(null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_firstParamNull() {
    merger.isMergeable(null, createTargetType());
  }

  @Test(expected = NullPointerException.class)
  public void testIsMergeable_secondParamNull() {
    merger.isMergeable(createTargetType(), null);
  }

  /**
   * Tests {@link AbstractMerger#merge(Object, Object)} does what the subclass
   * implementation desires.
   */
  @Test
  public abstract void testMerge() throws Exception;

  /**
   * Tests {@link AbstractMerger#merge(Object, Object)} does not modify the
   * content of the parameters.
   */
  @Test
  public abstract void testMerge_notModifyParams() throws Exception;

  @Test(expected = NullPointerException.class)
  public void testMerge_bothParamNull() {
    merger.merge(null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_firstParamNull() {
    merger.merge(null, createTargetType());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_secondParamNull() {
    merger.merge(createTargetType(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMerger_withNotMergeableParam() {
    merger.merge(createTargetType(), createTargetTypeDiff());
  }

  /**
   * Creates a merger for testing.
   */
  protected abstract AbstractMerger<T> createMerger();

  /**
   * Creates a target type for testing, all fields must be filled. Subsequence
   * calls to this method must return objects with exact same values.
   */
  protected abstract T createTargetType();

  /**
   * Creates a target type for testing, all fields must be filled, and is
   * different to {@link #createTargetType()}, i.e. calling {@code
   * AbstractMerger.isMergeable(createTargetType(), createTargetType2())} will
   * always return false. Subsequence calls to this method must return objects 
   * with exact same values.
   */
  protected abstract T createTargetTypeDiff();
}
