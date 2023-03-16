/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.toohandsome.httproxy.util;

import lombok.Synchronized;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;


public final class Queues {


  public static <E> int drain(
      BlockingQueue<E> q,
      Collection<? super E> buffer,
      int numElements,
      long timeout,
      TimeUnit unit)
      throws InterruptedException {

    /*
     * This code performs one System.nanoTime() more than necessary, and in return, the time to
     * execute Queue#drainTo is not added *on top* of waiting for the timeout (which could make
     * the timeout arbitrarily inaccurate, given a queue that is slow to drain).
     */
    long deadline = System.nanoTime() + unit.toNanos(timeout);
    int added = 0;
    while (added < numElements) {
      // we could rely solely on #poll, but #drainTo might be more efficient when there are multiple
      // elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
      added += q.drainTo(buffer, numElements - added);
      if (added < numElements) { // not enough elements immediately available; will have to poll
        E e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
        if (e == null) {
          break; // we already waited enough, and there are no more elements in sight
        }
        buffer.add(e);
        added++;
      }
    }
    return added;
  }

}
