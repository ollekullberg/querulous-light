package com.twitter.querulous.unit

import scala.collection.mutable.Map
import org.specs.Specification
import org.specs.mock.{ClassMocker, JMocker}
import com.twitter.querulous.database.StatsCollectingDatabase
import com.twitter.querulous.test.{FakeStatsCollector, FakeDatabase}
import com.twitter.xrayspecs.Time
import com.twitter.xrayspecs.TimeConversions._

object StatsCollectingDatabaseSpec extends Specification with JMocker with ClassMocker {
  "StatsCollectingDatabase" should {
    Time.freeze()
    val latency = 1.second
    val connection = mock[Connection]
    val stats = new FakeStatsCollector
    val pool = new StatsCollectingDatabase(new FakeDatabase(connection, latency), stats)

    "collect stats" in {
      "when releasing" >> {
        pool.release(connection)
        stats.times("connection-pool-release-timing") mustEqual latency.inMillis
      }

      "when reserving" >> {
        pool.reserve()
        stats.times("connection-pool-reserve-timing") mustEqual latency.inMillis
      }
    }
  }
}
