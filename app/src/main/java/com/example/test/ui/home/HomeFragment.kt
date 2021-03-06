package com.example.test.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.face_reg.FaceRecogActivity
import com.example.test.music_app.MusicGenActivity
import com.example.test.plt_reg.PlantRecogActivity

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        _binding!!.buttonMainFR.setOnClickListener {
            val intent = Intent(activity, FaceRecogActivity::class.java)
            startActivity(intent)
        }
        _binding!!.buttonMainSR.setOnClickListener {
            val intent = Intent(activity, PlantRecogActivity::class.java)
            startActivity(intent)
        }
        _binding!!.buttonMainGM.setOnClickListener {
            val intent = Intent(activity, MusicGenActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}