package ru.netology.nework.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.DialogAddMentionedUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.recyclerView.mentionUser.MentionUserAdapter
import ru.netology.nework.recyclerView.mentionUser.MentionUserListener
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewModel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AddMentionUser @Inject constructor(
    private val userViewModel: UserViewModel
) : DialogFragment() {

    private val userObserver : Observer<List<User>> = Observer<List<User>>{
        adapter.submitList(userViewModel.setupList())
    }

    private lateinit var binding : DialogAddMentionedUserBinding
    private lateinit var adapter : MentionUserAdapter
    private lateinit var builder : AlertDialog.Builder
    private lateinit var dialog : AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initBinding()
        setupAdapter()
        setupObserver()
        setupListeners()
        buildDialog()
        dismissListener()
        return dialog
    }

    private fun initBinding() {
        binding = DialogAddMentionedUserBinding.inflate(layoutInflater)
        binding.userGroup.visibility = View.GONE
    }

    private fun setupAdapter() {
        adapter = MentionUserAdapter(object : MentionUserListener {
            override fun chooseUser(user: User) {
                userViewModel.addSelectedUser(user)
                binding.apply {
                    if (user.avatar != null) {
                        Glide.with(userAvatar)
                            .load(user.avatar)
                            .placeholder(R.drawable.outline_account_circle_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .apply(RequestOptions().circleCrop())
                            .into(userAvatar)
                    } else {
                        userAvatar.setImageResource(R.drawable.outline_account_circle_24)
                    }
                    userName.text = user.name
                    userGroup.visibility = View.VISIBLE
                    findUserGroup.visibility = View.GONE
                }
                AndroidUtils.hideKeyboard(binding.root)
            }
        })
        binding.userList.adapter = adapter
    }

    private fun setupObserver() {
        userViewModel.data.observeForever(userObserver)
    }

    private fun buildDialog() {
        builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupListeners() {
        binding.apply {
            deleteUser.setOnClickListener {
                userViewModel.clearSelectedUser()
                userGroup.visibility = View.GONE
                findUserGroup.visibility = View.VISIBLE
            }
            addButton.setOnClickListener {
                if (userViewModel.saveSelectedUser()) {
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.oops_something_went_wrong), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            name.doAfterTextChanged {
                userViewModel.userFilter(it.toString())
                if (userViewModel.filterList.isNotEmpty()) {
                    adapter.submitList(userViewModel.filterList)
                }
            }
        }
    }
    private fun dismissListener() {
        dialog.setOnDismissListener {
            userViewModel.clearSelectedUser()
            userViewModel.data.removeObserver(userObserver)
        }
    }

}