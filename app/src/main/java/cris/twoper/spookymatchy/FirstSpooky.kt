package cris.twoper.spookymatchy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cris.twoper.spookymatchy.adapter.SpookyAdapter
import cris.twoper.spookymatchy.adapter.SpookyModel
import cris.twoper.spookymatchy.databinding.FragmentFirstSpookyBinding

class FirstSpooky : Fragment() {

    private var _binding: FragmentFirstSpookyBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SpookyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstSpookyBinding.inflate(inflater, container, false)

        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        val data = listOf(
            SpookyModel("Item 1")
        )
        adapter = SpookyAdapter(data)
        binding.recyclerview.adapter = adapter

        return binding.root
    }
}