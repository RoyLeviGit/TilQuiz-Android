package madortil.medicBook.models

/**
 * Created by danfechtmann on 07/03/2018.
 */

class pdfFile(var category: Int, var page: Int, byteData: ByteArray?) {
    var byteData: ByteArray? = null

    init {
        this.byteData = byteData
    }

}
