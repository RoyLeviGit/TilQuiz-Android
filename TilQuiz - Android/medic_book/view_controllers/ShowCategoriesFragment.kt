package madortil.medicBook.viewControllers


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_show_categories.*
import madortil.medicBook.CategoryAndPageFIreManipulator
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.HourPeriod
import madortil.medicBook.models.hourPeriod

/**
 * A simple [Fragment] subclass.
 *
 */
class ShowCategoriesFragment : Fragment() {

    private val _categoryStringArray = arrayOfNulls<String>(DataHolder.categories!!.size)
    private var currentLongClickItemPos = 0
    private var manipulator: CategoryAndPageFIreManipulator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_show_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manipulator = CategoryAndPageFIreManipulator(context!!)
        setCategoryStringArray()
        setUpUIElements()
        activity!!.title = "מדיקבוק"
    }

    private fun setUpUIElements() {
        listview_categories.setBackgroundResource(
                when (hourPeriod) {
                    HourPeriod.Morning -> R.drawable.ic_screen_background_morningr
                    HourPeriod.Noon -> R.drawable.ic_screen_background_noonr
                    else -> R.drawable.ic_screen_background_nightr
                })
        val adapter = ArrayAdapter<String>(context!!, R.layout.category_cell, _categoryStringArray)
        listview_categories.adapter = adapter
        DataHolder.adaptersToUpdate.add(adapter)
        listview_categories.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> onCategoryClick(position) }
        listview_categories.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            currentLongClickItemPos = position
            DataHolder.currentCategory = position
            false
        }
        registerForContextMenu(listview_categories)

    }

    private fun onCategoryClick(pos: Int) {
        DataHolder.currentCategory = pos
        findNavController().navigate(ShowCategoriesFragmentDirections.actionShowCategoriesFragmentToShowPagesFragment())
    }

    private fun setCategoryStringArray() {
        for (i in _categoryStringArray.indices) {
            _categoryStringArray[i] = DataHolder.categories!![i].name
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (DataHolder.isAdmin) {
            super.onCreateContextMenu(menu, v, menuInfo)
            val inflater = activity!!.menuInflater
            inflater.inflate(R.menu.edit_page, menu)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (DataHolder.isAdmin) {
            inflater.inflate(R.menu.add_new_category_and_page, menu)
        }
    }


    //TODO check why creating a category prints a mistake
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        progressbar_catLoading.visibility = View.VISIBLE
        manipulator!!.createCategory(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        progressbar_catLoading.visibility = View.VISIBLE
        if (R.id.editPage == item.itemId) {
            startEdit()
        } else {
            manipulator!!.deleteCategory(this, DataHolder.categories!![DataHolder.currentCategory])
        }
        return super.onContextItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DataHolder.updateAdapters()
        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
    }

    private fun startEdit() {
        findNavController().navigate(
                ShowCategoriesFragmentDirections.actionShowCategoriesFragmentToChangeCategoryAndPageNamesFragment(DataHolder.ItemType.CATEGORY, _categoryStringArray[currentLongClickItemPos]!!))
    }
}
