package madortil.medicBook.viewControllers


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_show_pages.*
import madortil.medicBook.CategoryAndPageFIreManipulator
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.HourPeriod
import madortil.medicBook.models.hourPeriod

/**
 * A simple [Fragment] subclass.
 *
 */
class ShowPagesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_show_pages, container, false)
    }

    private var _pageNamesArray = arrayOfNulls<String>(DataHolder.categories!![DataHolder.currentCategory].pages.size)
    private var manipulator: CategoryAndPageFIreManipulator? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manipulator = CategoryAndPageFIreManipulator(context!!)
        setPageNamesArray()
        setUpUI()
        activity?.title = DataHolder.categories!![DataHolder.currentCategory].name
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (DataHolder.isAdmin) {
            inflater.inflate(R.menu.add_new_category_and_page, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        progressbar_deletePage.visibility = View.VISIBLE
        val categoryKey = DataHolder.categories!![DataHolder.currentCategory].key
        manipulator!!.createNewPage(categoryKey, this)

        return super.onOptionsItemSelected(item)
    }
    private fun setUpUI() {
        categoryThemeImageView.setBackgroundResource(
                when (hourPeriod) {
                    HourPeriod.Morning -> R.drawable.ic_screen_background_morningr
                    HourPeriod.Noon -> R.drawable.ic_screen_background_noonr
                    else -> R.drawable.ic_screen_background_nightr
                })
        categoryThemeImageView.setImageResource(categoryThemeGlowImage(DataHolder.currentCategory))

        val adapter = ArrayAdapter<String>(context!!, R.layout.page_cell, _pageNamesArray)
        listview_pages.adapter = adapter
        DataHolder.adaptersToUpdate.add(adapter)
        listview_pages.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> onPageClickDisplayPDF(position) }
        listview_pages.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            DataHolder.currentPage = position
            false
        }
        registerForContextMenu(listview_pages)
    }
    private fun categoryThemeGlowImage(byInt: Int): Int {
        return when(byInt%10) {
            0 -> R.drawable.ic_category_theme_glow_1
            1 -> R.drawable.ic_category_theme_glow_2
            2 -> R.drawable.ic_category_theme_glow_3
            3 -> R.drawable.ic_category_theme_glow_4
            4 -> R.drawable.ic_category_theme_glow_5
            5 -> R.drawable.ic_category_theme_glow_6
            6 -> R.drawable.ic_category_theme_glow_7
            7 -> R.drawable.ic_category_theme_glow_8
            8 -> R.drawable.ic_category_theme_glow_9
            else -> R.drawable.ic_category_theme_glow_10
        }
    }

    private fun onPageClickDisplayPDF(pos: Int) {
        DataHolder.currentPage = pos
        findNavController().navigate(
                ShowPagesFragmentDirections.actionShowPagesFragmentToDisplayPdfFilesFragment()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DataHolder.updateAdapters()
        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
    }

    private fun setPageNamesArray() {
        val currentCat = DataHolder.currentCategory
//        DataHolder.categories!![currentCat].pages.sortBy { it.name }
        val pages = DataHolder.categories!![currentCat].pages

        _pageNamesArray = arrayOfNulls(DataHolder.categories!![DataHolder.currentCategory].pages.size)
        for (i in pages.indices) {
            _pageNamesArray[i] = pages[i].name
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (DataHolder.isAdmin) {
            super.onCreateContextMenu(menu, v, menuInfo)
            val inflater = MenuInflater(context)
            inflater.inflate(R.menu.edit_page, menu)
        }
    }

    //TODO add loading indicator and disable ui when deleting
    override fun onContextItemSelected(item: MenuItem): Boolean {
        progressbar_deletePage.visibility = View.VISIBLE
        if (item.itemId == R.id.editPage) {
            findNavController().navigate(
                    ShowPagesFragmentDirections.actionShowPagesFragmentToChangeCategoryAndPageNamesFragment(DataHolder.ItemType.PAGE, _pageNamesArray[DataHolder.currentPage]!!)
            )
        } else {
            manipulator!!.deletePage(this, DataHolder.currentPage, DataHolder.categories!![DataHolder.currentCategory])
        }
        return super.onContextItemSelected(item)
    }
}
