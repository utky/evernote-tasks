package ilyaletre.evernote.tasks

import org.scalatest.funspec.AnyFunSpec
import java.time.{ZonedDateTime, LocalDate, LocalTime, ZoneId}

class QuerySpec extends AnyFunSpec {
  describe("Query") {
    it("should convert single predicate to string") {
      assert(Query.fromPredicates(Seq(Notebook("test")))
        == "notebook:test")
    }
    it("should convert negative predicate to string") {
      assert(Query.fromPredicates(Seq(Not(Notebook("test"))))
        == "-notebook:test")
    }
    it("should convert predicates to string") {
      assert(Query.fromPredicates(
        Seq(Notebook("testnb"), Title("test title")))
        == "notebook:testnb intitle:\"test title\"")
    }
  }
  describe("TimeSpan") {
    it("convert time at") {
      assert(DaysBefore(3).toString() == "day-3")
    }
    it("days ago") {
      assert(At(java.time.Instant.ofEpochSecond(1597014960)).toString()
        == "20200809T231600Z")
    }
    it("days ago with timezone") {
      val dt = ZonedDateTime.of(
        LocalDate.of(2020, 8, 10),
        LocalTime.of(8, 16, 0),
        ZoneId.of("Asia/Tokyo")
      ).toInstant()
      assert(At(dt).toString()
        == "20200809T231600Z")
    }
  }
}

