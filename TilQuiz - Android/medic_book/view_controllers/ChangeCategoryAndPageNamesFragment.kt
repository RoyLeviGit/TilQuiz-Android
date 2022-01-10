package madortil.medicBook.viewControllers


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_change_category_and_page_names.*
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.HourPeriod
import madortil.medicBook.models.hourPeriod
import madortil.medicBook.models.keys
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 *
 */
class ChangeCategoryAndPageNamesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_category_and_page_names, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUIElements()
        activity?.title = "שנה שם"
    }

    private fun setUpUIElements() {
        currentName.text = ChangeCategoryAndPageNamesFragmentArgs.fromBundle(arguments!!).name
        backgroundImageView.setImageResource(
                when (hourPeriod) {
                    HourPeriod.Morning -> R.drawable.ic_screen_background_morningr
                    HourPeriod.Noon -> R.drawable.ic_screen_background_noonr
                    else -> R.drawable.ic_screen_background_nightr
                })
        confirmChange.setOnClickListener { confirmButtonOnClick() }
    }

    private fun disableActivity(status: Boolean) {
        if (status) {
            progressbar_changeName.visibility = View.VISIBLE
//            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            progressbar_changeName.visibility = View.GONE
//            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun confirmButtonOnClick() {
        disableActivity(true)
        val catKey = DataHolder.categories!![DataHolder.currentCategory].key
        val pgeKey = DataHolder.categories!![DataHolder.currentCategory].pages[DataHolder.currentPage].pageKey

        val type = ChangeCategoryAndPageNamesFragmentArgs.fromBundle(arguments!!).type
        if (type == DataHolder.ItemType.CATEGORY) {
            val childValues = HashMap<String, Any>()
            childValues["name"] = editName.text.toString()
            FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).child(catKey).updateChildren(childValues).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "שונה בהצלחה", Toast.LENGTH_SHORT).show()
                    disableActivity(false)
                    activity!!.setResult(0)
//                    activity!!.finish()
                    findNavController().popBackStack()
                }
            }
        } else if (type == DataHolder.ItemType.PAGE) {
            val updatedValues = HashMap<String, Any>()
            updatedValues["name"] = editName.text.toString()
            FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).child(catKey).child(keys.PAGES).child(pgeKey).updateChildren(updatedValues).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "שונה בהצלחה", Toast.LENGTH_SHORT).show()
                    activity!!.setResult(0)
//                    activity!!.finish()
                    findNavController().popBackStack()
                }
            }
        }
    }

}
