package daweb3



import org.junit.*
import grails.test.mixin.*

@TestFor(QueueEntryController)
@Mock(QueueEntry)
class QueueEntryControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/queueEntry/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.queueEntryInstanceList.size() == 0
        assert model.queueEntryInstanceTotal == 0
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/queueEntry/list'


        populateValidParams(params)
        def queueEntry = new QueueEntry(params)

        assert queueEntry.save() != null

        params.id = queueEntry.id

        def model = controller.show()

        assert model.queueEntryInstance == queueEntry
    }

}
