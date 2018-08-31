import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object ComponentSpec : Spek({

    describe("When waiting for a component") {
        val testSubject = ComponentManager<Unit>()
        val obs = testSubject.component.test()

        it("should not emit anything") {
            obs.assertNoValues().assertNotComplete().assertNoErrors()
        }
    }

    describe("When a component becomes available") {
        val testSubject = ComponentManager<Unit>()
        val obs = testSubject.component.test()
        testSubject.consume(Unit)

        it("should emit the component") {
            obs.assertValueCount(1).assertComplete()
        }
    }

    describe("When clear is executed with no available component") {
        val testSubject = ComponentManager<Unit>()
        val obs = testSubject.component.test()
        testSubject.clear()

        it("should not emit anything") {
            obs.assertNoValues().assertNotComplete().assertNoErrors()
        }
    }

    describe("When a component is cleared") {
        val testSubject = ComponentManager<Unit>()
        testSubject.consume(Unit)
        testSubject.clear()
        val obs = testSubject.component.test()

        it("should no longer be emitted") {
            obs.assertNotTerminated()
        }
    }

    describe("When I replace my component with a new one") {
        val testSubject = ComponentManager<String>()
        testSubject.consume("A")
        testSubject.consume("B")
        val obs = testSubject.component.test()

        it("should only ever emit b") {
            obs.assertValue("B")
        }
    }
})
