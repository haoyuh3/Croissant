package com.bytedance.croissantapp.presentation.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.bytedance.croissantapp.data.local.UserPreferencesRepository

data class ProfileUiState(
    val nickname: String = "用户昵称",
    val bio: String = "这里是个人简介",
    val avatarUri: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.value = ProfileUiState(
            nickname = preferencesRepository.getUserNickname(),
            bio = preferencesRepository.getUserBio(),
            avatarUri = preferencesRepository.getUserAvatar()
        )
    }

    fun updateNickname(nickname: String) {
        preferencesRepository.setUserNickname(nickname)
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }

    fun updateBio(bio: String) {
        preferencesRepository.setUserBio(bio)
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun updateAvatar(uri: String) {
        preferencesRepository.setUserAvatar(uri)
        _uiState.value = _uiState.value.copy(avatarUri = uri)
    }
}
