package com.hazelcast.partition;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.core.PartitionService;
import com.hazelcast.internal.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelTest.class})
public class PartitionMigrationListenerTest extends HazelcastTestSupport {


    @Test
    public void testMigrationListenerCalledOnlyOnceWhenMigrationHappens() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config config = new Config();
        int partitionCount = 10;
        config.setProperty(GroupProperty.PARTITION_COUNT.getName(), String.valueOf(partitionCount));
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        CountingMigrationListener migrationListener = new CountingMigrationListener(partitionCount);
        instance.getPartitionService().addMigrationListener(migrationListener);
        IMap<Object, Object> aDefault = instance.getMap(randomName());
        aDefault.put(1, 1);
        factory.newHazelcastInstance(config);
        assertAllLesserOrEquals(migrationListener.migrationStarted, 1);
        assertAllLesserOrEquals(migrationListener.migrationCompleted, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testAddMigrationListener_whenNullListener() {
        HazelcastInstance hz = createHazelcastInstance();
        PartitionService partitionService = hz.getPartitionService();

        partitionService.addMigrationListener(null);
    }

    @Test
    public void testAddMigrationListener_whenListenerRegisteredTwice() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        PartitionService partitionService = hz1.getPartitionService();

        MigrationListener listener = mock(MigrationListener.class);

        String id1 = partitionService.addMigrationListener(listener);
        String id2 = partitionService.addMigrationListener(listener);

        // first we check if the registration id's are different
        assertNotEquals(id1, id2);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveMigrationListener_whenNullListener() {
        HazelcastInstance hz = createHazelcastInstance();
        PartitionService partitionService = hz.getPartitionService();

        partitionService.removeMigrationListener(null);
    }

    @Test
    public void testRemoveMigrationListener_whenNonExistingRegistrationId() {
        HazelcastInstance hz = createHazelcastInstance();
        PartitionService partitionService = hz.getPartitionService();

        boolean result = partitionService.removeMigrationListener("notexist");

        assertFalse(result);
    }

    @Test
    public void testRemoveMigrationListener() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        PartitionService partitionService = hz1.getPartitionService();

        MigrationListener listener = mock(MigrationListener.class);

        String id = partitionService.addMigrationListener(listener);
        boolean removed = partitionService.removeMigrationListener(id);

        assertTrue(removed);

        // now we add a member
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        warmUpPartitions(hz1, hz2);

        // and verify that the listener isn't called.
        verify(listener, never()).migrationStarted(any(MigrationEvent.class));
    }


    private void assertAllLesserOrEquals(AtomicInteger[] integers, int expected) {
        for (AtomicInteger integer : integers) {
            assertTrue(integer.get() <= expected);
        }
    }

    class CountingMigrationListener implements MigrationListener {

        AtomicInteger[] migrationStarted;
        AtomicInteger[] migrationCompleted;
        AtomicInteger[] migrationFailed;

        public CountingMigrationListener(int partitionCount) {
            migrationStarted = new AtomicInteger[partitionCount];
            migrationCompleted = new AtomicInteger[partitionCount];
            migrationFailed = new AtomicInteger[partitionCount];
            for (int i = 0; i < partitionCount; i++) {
                migrationStarted[i] = new AtomicInteger();
                migrationCompleted[i] = new AtomicInteger();
                migrationFailed[i] = new AtomicInteger();
            }
        }

        @Override
        public void migrationStarted(MigrationEvent migrationEvent) {
            migrationStarted[migrationEvent.getPartitionId()].incrementAndGet();
        }

        @Override
        public void migrationCompleted(MigrationEvent migrationEvent) {
            migrationCompleted[migrationEvent.getPartitionId()].incrementAndGet();
        }

        @Override
        public void migrationFailed(MigrationEvent migrationEvent) {
            migrationFailed[migrationEvent.getPartitionId()].incrementAndGet();
        }
    }
}
