package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.PartEventType;

import com.google.common.collect.Lists;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @see Mergers
 */
public class MergersTest {

  @Test
  public void testMerge_collectionAndElement_mergerNull() {
    Collection<String> data = Lists.newArrayList("1");
    assertThat(Mergers.merge(null, data, "2"), sameInstance(data));
    assertThat(data.size(), equalTo(2));
    assertThat(data, hasItems("1", "2"));
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndElement_collectionNull() {
    Mergers.merge(new PartEventTypeMerger(), null, new PartEventType());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndElement_elementNull() {
    Mergers.merge(new PartEventTypeMerger(), 
        Collections.<PartEventType> emptyList(), null);
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndCollection_firstCollectionNull() {
    Mergers.merge(new PartEventTypeMerger(), null, Collections
        .<PartEventType> emptyList());
  }

  @Test(expected = NullPointerException.class)
  public void testMerge_collectionAndCollection_secondCollectionNull() {
    Mergers.merge(new PartEventTypeMerger(), Collections
        .<PartEventType> emptyList(), null);
  }

  @Test
  public void testMerge_collectionAndCollection_mergerNull() {
    Collection<Integer> main = Lists.newArrayList(1);
    Collection<Integer> sub = Lists.newArrayList(2);
    Collection<Integer> subClone = Lists.newArrayList(sub);
    assertThat(Mergers.merge(null, main, sub), sameInstance(main));
    
    // Assert "main" is changed:
    assertThat(main.size(), equalTo(2));
    assertThat(main, hasItems(1, 2));
    
    // Assert "sub" is unchanged:
    assertThat(sub, equalTo(subClone));
  }

  @Test
  public void testMerge_collectionAndElement_mergeableElements() {
    String id = "abc";
    PartEventType type1 = new PartEventType();
    type1.setDuration(11);
    type1.setPartId(id);
    PartEventType type2 = new PartEventType();
    type2.setDuration(9823);
    type2.setPartId(id);
    IMerger<PartEventType> merger = new PartEventTypeMerger();
    // Check the elements we just created are mergeable
    assertTrue(merger.isMergeable(type1, type2));

    List<PartEventType> elements = Lists.newArrayList(type1);
    assertSame(elements, Mergers.merge(merger, elements, type2));

    // Check the elements are merged:
    assertEquals(1, elements.size());
    PartEventType result = elements.get(0);
    assertEquals(id, result.getPartId());
    assertEquals(type1.getDuration() + type2.getDuration(), 
        result.getDuration());
  }
  
  @Test
  public void testMerge_collectionAndElement_unmergeableElements() {
    String id1 = "13458";
    String id2 = "abcdef";
    long duration1 = 139834;
    long duration2 = 983471;
    PartEventType type1 = new PartEventType();
    PartEventType type2 = new PartEventType();
    type1.setDuration(duration1);
    type2.setDuration(duration2);
    type1.setPartId(id1);
    type2.setPartId(id2);
    
    IMerger<PartEventType> merger = new PartEventTypeMerger();
    // Check the elements we just created are not mergeable
    assertFalse(merger.isMergeable(type1, type2));

    List<PartEventType> collection = Lists.newArrayList(type1);
    assertSame(collection, Mergers.merge(merger, collection, type2));

    // Check the elements are merged:
    assertEquals(2, collection.size());
    assertSame(type1, collection.get(0));
    assertSame(type2, collection.get(1));
    assertEquals(id1, type1.getPartId());
    assertEquals(id2, type2.getPartId());
    assertEquals(duration1, type1.getDuration());
    assertEquals(duration2, type2.getDuration());
  }
}
