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

    void testCreate() {
       def model = controller.create()

       assert model.queueEntryInstance != null
    }

    void testSave() {
        controller.save()

        assert model.queueEntryInstance != null
        assert view == '/queueEntry/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/queueEntry/show/1'
        assert controller.flash.message != null
        assert QueueEntry.count() == 1
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

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/queueEntry/list'


        populateValidParams(params)
        def queueEntry = new QueueEntry(params)

        assert queueEntry.save() != null

        params.id = queueEntry.id

        def model = controller.edit()

        assert model.queueEntryInstance == queueEntry
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/queueEntry/list'

        response.reset()


        populateValidParams(params)
        def queueEntry = new QueueEntry(params)

        assert queueEntry.save() != null

        // test invalid parameters in update
        params.id = queueEntry.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/queueEntry/edit"
        assert model.queueEntryInstance != null

        queueEntry.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/queueEntry/show/$queueEntry.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        queueEntry.clearErrors()

        populateValidParams(params)
        params.id = queueEntry.id
        params.version = -1
        controller.update()

        assert view == "/queueEntry/edit"
        assert model.queueEntryInstance != null
        assert model.queueEntryInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/queueEntry/list'

        response.reset()

        populateValidParams(params)
        def queueEntry = new QueueEntry(params)

        assert queueEntry.save() != null
        assert QueueEntry.count() == 1

        params.id = queueEntry.id

        controller.delete()

        assert QueueEntry.count() == 0
        assert QueueEntry.get(queueEntry.id) == null
        assert response.redirectedUrl == '/queueEntry/list'
    }
}
