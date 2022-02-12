package mgt.android.performance.scroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragment : Fragment() {
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt(ARG_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // simulate long layout inflation, increase to experience the improvement more clearly
        Thread.sleep(150)
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).checkSetLowPageLimit()
        indexTextView.text = index.toString()
    }

    companion object {
        private const val ARG_INDEX = "ARG_INDEX"

        fun newInstance(index: Int) =
            ItemFragment().apply {
                arguments = bundleOf(
                    ARG_INDEX to index
                )
            }
    }
}
