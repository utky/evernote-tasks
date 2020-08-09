package ilyaletre.evernote.tasks

import org.scalatest.funspec.AnyFunSpec

class QuerySpec extends AnyFunSpec {
  describe("Query") {
    it("should convert single predicate to string") {
      assert(Query.fromPredicates(Seq(Notebook("test")))
        == "notebook:test")
    }
    it("should convert predicates to string") {
      assert(Query.fromPredicates(
        Seq(Notebook("testnb"), Title("test title")))
        == "notebook:testnb intitle:\"test title\"")
    }
  }

}

