package madortil.medicBook.viewControllers


import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_display_pdf_files.*
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.pdfFile
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * A simple [Fragment] subclass.
 *
 */
class DisplayPdfFilesFragment : Fragment() {

    private var PICK_PDF_CODE = 1
    private var cat = 0
    private var pge = 0
    private var pdfURL = ""
    private lateinit var document: PdfDocument
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_pdf_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        document = PdfDocument()
        cat = DataHolder.currentCategory
        pge = DataHolder.currentPage
        pdfURL = DataHolder.categories!![cat].pages[pge].url
        doesPDFExist(cat, pge, pdfURL)
        activity!!.title = DataHolder.categories!![DataHolder.currentCategory].pages[DataHolder.currentPage].name
    }
    private fun disableActivity(status: Boolean) {
        if (status) {
            progressbar_downloadingPDF?.visibility = View.VISIBLE
            pdfscreen?.visibility = View.GONE
//            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            progressbar_downloadingPDF?.visibility = View.GONE
            pdfscreen?.visibility = View.VISIBLE
//            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (DataHolder.isAdmin) {
            inflater.inflate(R.menu.upload_pdf_actionbar_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        uploadButtonDidClick()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_CODE && resultCode == Activity.RESULT_OK) {
            val pdfData = data!!.data
            try {
                disableActivity(true)
                val inputStream = activity?.contentResolver?.openInputStream(pdfData!!)
                val pdfByteArray = getBytes(inputStream!!)
                Toast.makeText(context, "מבצע תהליכים", Toast.LENGTH_SHORT).show()
                uploadPDF(pdfByteArray)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun downloadPDF(cat: Int, pge: Int, url: String) {
        FirebaseStorage.getInstance().reference.child(url).getBytes(999999999).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadedData = task.result
                storePDFFile(cat, pge, downloadedData)
                pdfscreen?.fromBytes(downloadedData)?.load()
            } else {
                Toast.makeText(context, "קרתה שגיעה", Toast.LENGTH_SHORT).show()
            }
            disableActivity(false)
            progressbar_downloadingPDF?.visibility = View.INVISIBLE
        }
    }

    private fun uploadPDF(pdfByteArray: ByteArray) {
        disableActivity(true)
        val fileName = DataHolder.categories!![DataHolder.currentCategory].pages[DataHolder.currentPage].url
        FirebaseStorage.getInstance().reference.child(fileName).putBytes(pdfByteArray).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "הקובץ עלה בהצלחה", Toast.LENGTH_SHORT).show()
                downloadPDF(cat, pge, pdfURL)
            }
        }
    }

    private fun uploadButtonDidClick() {
        /*
        * once the user clicks the button a menu appears to let him select only pdf files
        * once he selects the files and confirms it begins upload and replaces the current file in the storage.
        * uploaded pdf files will have the same name as the current url to replace it, new pdf files will get their name by taking the name attribute of the page
        * */
        val uploadIntent = Intent()
        uploadIntent.type = "application/pdf"
        uploadIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(uploadIntent, PICK_PDF_CODE)
    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = inputStream.read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
        return byteBuffer.toByteArray()
    }

    private fun storePDFFile(cat: Int, page: Int, data: ByteArray?) {
        val file = pdfFile(cat, page, data)
        DataHolder.downloadedFiles.add(file)
    }

    private fun doesPDFExist(cat: Int, pge: Int, url: String) {
        disableActivity(true)
        downloadPDF(cat, pge, url)
    }
}
