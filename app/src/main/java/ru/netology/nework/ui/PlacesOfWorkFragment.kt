package ru.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPlacesOfWorkBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.recyclerView.placeOfWork.PlaceOfWorkAdapter
import ru.netology.nework.recyclerView.placeOfWork.PlaceOfWorkListener
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AppUtils
import ru.netology.nework.viewModel.JobsViewModel

class PlacesOfWorkFragment : Fragment() {
    private lateinit var binding: FragmentPlacesOfWorkBinding
    private lateinit var adapter: PlaceOfWorkAdapter
    private val viewModel: JobsViewModel by activityViewModels()
    private val args : PlacesOfWorkFragmentArgs by navArgs()
    private var jobCreated: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setupVisibility()
        initAdapter()
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentPlacesOfWorkBinding.inflate(
            inflater, container, false
        )
    }

    private fun setupVisibility() {
        val editJobGraph = args.editJobsGraph
        binding.apply {
            if (editJobGraph) {
                skipButton.visibility = View.GONE
                navContainer.visibility = View.VISIBLE
                addJobGroup.visibility = View.GONE
            } else {
                skipButton.visibility = View.VISIBLE
                navContainer.visibility = View.GONE
                addJobGroup.visibility = View.VISIBLE
            }
        }
    }

    private fun initAdapter() {
        adapter = PlaceOfWorkAdapter(object : PlaceOfWorkListener {
            //            override fun delete(jobs: Job) {
//                viewModel.delete(jobs)
//            }
//
//            override fun placeOfWorkChanged(jobs: Job) {
//                viewModel.placeOfWorkChanged(jobs)
//            }
//
//            override fun positionChanged(jobs: Job) {
//                viewModel.positionChanged(jobs)
//            }
//
//            override fun linkChanged(jobs: Job) {
//                viewModel.linkChanged(jobs)
//            }
//
//            override fun fromChanged(jobs: Job, position: Int) {
//                viewModel.fromChanged(jobs)
//            }
//
//            override fun toChanged(jobs: Job, position: Int) {
//                viewModel.toChanged(jobs)
            override fun edit(job: Job) {
                binding.apply {
                    placeOfWork.setText(job.name)
                    position.setText(job.position)
                    fromMonth.setText(AppUtils.getMonth(job.start))
                    fromYear.setText(AppUtils.getYear(job.start))
                    job.finish?.let {
                        toMonth.setText(AppUtils.getMonth(job.finish))
                        toYear.setText(AppUtils.getYear(job.finish))
                    }
                    job.link?.let {
                        link.setText(job.link)
                    }
                    addJobGroup.visibility = View.VISIBLE
                    jobCreated = job
                }
            }

            override fun delete(id: Int) {
                viewModel.delete(id)
            }
        })
        binding.list.adapter = adapter

    }

    private fun setupListeners() {
        binding.apply {
            addMoreButton.setOnClickListener {
//                viewModel.addMore()
                binding.addJobGroup.visibility = View.VISIBLE
            }
            sendJobs.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                if (checkFieldsInCorrectly()) {
                    sendJobs()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.required_fields_cannot_be_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            skipButton.setOnClickListener {
                findNavController().navigate(R.id.placesOfWorkFragmentToPostsWallFragment)
            }
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            deleteJobs.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                jobCreated = null
                clearEditText()
                if (previewJobGroup.visibility == View.VISIBLE) {
                    addJobGroup.visibility = View.GONE
                }
            }
        }
    }

    private fun checkFieldsInCorrectly(): Boolean {
        binding.apply {
            val emptyChecked = !(placeOfWork.text.toString().isEmpty() || position.text.toString()
                .isEmpty() || fromMonth.text.toString().isEmpty() || fromYear.text.toString()
                .isEmpty())
            val startMonthCorrectChecked = fromMonth.text.toString().length == 2
            val startYearCorrectChecked = fromYear.text.toString().length == 4
            var finishMonthCorrectChecked = true
            var finishYearCorrectChecked = true
            if (toMonth.text.toString().isNotEmpty() || toYear.text.toString().isNotEmpty()) {
                finishMonthCorrectChecked = toMonth.text.toString().length == 2
                finishYearCorrectChecked = toYear.text.toString().length == 4
            }
            return emptyChecked && startMonthCorrectChecked && startYearCorrectChecked && finishMonthCorrectChecked && finishYearCorrectChecked
        }
    }

    private fun sendJobs() {
        if (jobCreated != null) {
            val editedJob = getEditedJob() ?: return
            viewModel.sendJob(editedJob)
        } else {
            val newJob = createNewJob() ?: return
            viewModel.sendJob(newJob)
        }
        jobCreated = null
    }

    private fun getEditedJob(): Job? {
        binding.apply {
            jobCreated?.let { oldJob ->
                val newJob = createNewJob()
                if (newJob == null) {
                    Toast.makeText(
                        requireContext(),
                        R.string.oops,
                        Toast.LENGTH_SHORT
                    ).show()
                    return null
                }
                return viewModel.getEditedJob(oldJob, newJob)
            }
            return null
        }
    }


    private fun createNewJob(): Job? {
        binding.apply {
            val oldJob = jobCreated

            val newStart = if (oldJob != null) AppUtils.setMonthAndYear(
                fromMonth.text.toString(), fromYear.text.toString(), oldJob.start
            ) else AppUtils.setMonthAndYear(
                fromMonth.text.toString(), fromYear.text.toString(), null)

            val newFinish = if (oldJob != null) AppUtils.setMonthAndYear(
                toMonth.text.toString(), toYear.text.toString(), oldJob.finish
            ) else AppUtils.setMonthAndYear(
                toMonth.text.toString(), toYear.text.toString(), null)

            val newLink: String? = if (link.text.isNullOrBlank()) null else link.text.toString()
            if (newStart != null) {
                return Job(
                    0,
                    placeOfWork.text.toString(),
                    position.text.toString(),
                    newStart,
                    newFinish,
                    newLink
                )
            }
            Toast.makeText(
                requireContext(),
                R.string.oops,
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
    }

    private fun setupObservers() {
//        viewModel.createJobs.observe(viewLifecycleOwner) {
//            adapter.submitList(it)
//        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.apply {
                progressBar.isVisible = state.loading
                if (state.idle) {
                    clearEditText()
//                    findNavController().navigate(R.id.placesOfWorkFragmentToPostsWallFragment)
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getText(it), Toast.LENGTH_SHORT).show()
        }
        viewModel.userJobs.observe(viewLifecycleOwner) { jobs ->
//            viewModel.setUserJobs()
            binding.apply {
                if (jobs != null && jobs.isNotEmpty()) {
                    adapter.submitList(jobs)
                    previewJobGroup.visibility = View.VISIBLE
                    addJobGroup.visibility = View.GONE
//                    skipButton.visibility = View.GONE
//                    navContainer.visibility = View.VISIBLE
                } else {
                    previewJobGroup.visibility = View.GONE
                    addJobGroup.visibility = View.VISIBLE
//                    skipButton.visibility = View.VISIBLE
//                    navContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun clearEditText() {
        binding.apply {
            placeOfWork.text = null
            position.text = null
            fromMonth.text = null
            fromYear.text = null
            toMonth.text = null
            toYear.text = null
            link.text = null
        }
    }
}