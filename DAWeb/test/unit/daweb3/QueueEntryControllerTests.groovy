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

}
