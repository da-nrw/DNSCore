package daweb3



import org.junit.*
import grails.test.mixin.*

@TestFor(ObjectController)
@Mock(Object)
class ObjectControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/object/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.objectInstanceList.size() == 0
        assert model.objectInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.objectInstance != null
    }

    void testSave() {
        controller.save()

        assert model.objectInstance != null
        assert view == '/object/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/object/show/1'
        assert controller.flash.message != null
        assert Object.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/object/list'


        populateValidParams(params)
        def object = new Object(params)

        assert object.save() != null

        params.id = object.id

        def model = controller.show()

        assert model.objectInstance == object
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/object/list'


        populateValidParams(params)
        def object = new Object(params)

        assert object.save() != null

        params.id = object.id

        def model = controller.edit()

        assert model.objectInstance == object
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/object/list'

        response.reset()


        populateValidParams(params)
        def object = new Object(params)

        assert object.save() != null

        // test invalid parameters in update
        params.id = object.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/object/edit"
        assert model.objectInstance != null

        object.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/object/show/$object.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        object.clearErrors()

        populateValidParams(params)
        params.id = object.id
        params.version = -1
        controller.update()

        assert view == "/object/edit"
        assert model.objectInstance != null
        assert model.objectInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/object/list'

        response.reset()

        populateValidParams(params)
        def object = new Object(params)

        assert object.save() != null
        assert Object.count() == 1

        params.id = object.id

        controller.delete()

        assert Object.count() == 0
        assert Object.get(object.id) == null
        assert response.redirectedUrl == '/object/list'
    }
}
