package madortil.medicBook.models

import java.util.ArrayList

/**
 * Created by danfechtmann on 13/02/2018.
 */

class category(var key: String, var name: String, var categoryID: Int) {
    var pages: ArrayList<page>

    init {
        pages = ArrayList()
    }
}
