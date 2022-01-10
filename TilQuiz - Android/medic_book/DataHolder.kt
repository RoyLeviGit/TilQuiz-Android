package madortil.medicBook

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import madortil.medicBook.models.IDSearchResult
import madortil.medicBook.models.PDFCheckResult
import madortil.medicBook.models.category
import madortil.medicBook.models.pdfFile
import org.jetbrains.anko.async

import java.util.ArrayList

/**
 * Created by danfechtmann on 13/02/2018.
 */

object DataHolder {

    var categories: ArrayList<category>? = null

    var currentCategory = 0
    var currentPage = 0

    var isAdmin = false

    var downloadedFiles = ArrayList<pdfFile>()
    var adaptersToUpdate = ArrayList<ArrayAdapter<*>>()


    enum class ItemType {
        CATEGORY,
        PAGE,
        NO_ID
    }


    fun updateAdapters() {
        for (adapter in adaptersToUpdate) {
            adapter.notifyDataSetChanged()
        }
    }

    fun checkPDFDownloaded(cat: Int, pge: Int): PDFCheckResult {
        var file: pdfFile
        for (i in downloadedFiles.indices) {
            file = downloadedFiles[i]
            if (file.category == cat && file.page == pge && file.byteData != null) {
                return PDFCheckResult(PDFCheckResult.PDFCheckResultStatus.Success, file)
            }
        }
        return PDFCheckResult(PDFCheckResult.PDFCheckResultStatus.Fail, null)
    }

    fun SearchID(id: Int): IDSearchResult {
        for (cat in categories!!) {
            if (id == cat.categoryID) {
                return IDSearchResult(cat.name, ItemType.CATEGORY, null, cat.key)
            } else {
                for (p in cat.pages) {
                    if (p.pageID == id) {
                        return IDSearchResult(p.name, ItemType.PAGE, p.pageKey, cat.key)
                    }
                }
            }
        }
        return IDSearchResult("NULL_RESULT", ItemType.NO_ID, null, null)
    }


}
