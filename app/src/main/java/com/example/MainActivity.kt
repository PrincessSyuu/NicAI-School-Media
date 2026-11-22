package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.NovaDeckCloudRepository
import com.example.data.PreferencesRepository
import com.example.data.UserPreferencesState
import com.example.ui.components.AboutBertDeveloperDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NicAiScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.SlideStudioScreen
import com.example.ui.screens.StudyHubScreen
import com.example.ui.theme.NovaDeckTheme
import com.example.ui.viewmodel.NovaDeckViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

const val ROUTE_HOME = "home"
const val ROUTE_SLIDES = "slides"
const val ROUTE_NICAI = "nicai"
const val ROUTE_STUDY = "study"
const val ROUTE_PLANS = "plans"

private data class NavDestinationItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    NavDestinationItem(ROUTE_HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavDestinationItem(ROUTE_SLIDES, "Slides", Icons.Filled.Slideshow, Icons.Outlined.Slideshow),
    NavDestinationItem(ROUTE_NICAI, "NicAI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    NavDestinationItem(ROUTE_STUDY, "Study", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    NavDestinationItem(ROUTE_PLANS, "Plans", Icons.Filled.WorkspacePremium, Icons.Outlined.WorkspacePremium)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Firebase App Check before setContent, ViewModels, or Firebase operations.
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            NovaDeckAuthGate()
        }
    }
}

@Composable
fun NovaDeckAuthGate() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val prefsRepo = remember(context) { PreferencesRepository(context.applicationContext) }
    val localPrefs by prefsRepo.preferencesFlow.collectAsStateWithLifecycle(initialValue = UserPreferencesState())

    val currentUser by Firebase.auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    var isCheckingAutoSignIn by remember { mutableStateOf(currentUser == null) }
    var showAboutBertDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            isCheckingAutoSignIn = true
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                scope = scope,
                onAuthSuccess = { isCheckingAutoSignIn = false },
                onUnauthenticated = { isCheckingAutoSignIn = false }
            )
        } else {
            isCheckingAutoSignIn = false
        }
    }

    NovaDeckTheme(darkTheme = localPrefs.isDarkTheme) {
        val activeUser = currentUser
        if (activeUser == null) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                AuthScreen(
                    isCheckingAutoSignIn = isCheckingAutoSignIn,
                    onAuthSuccess = { isCheckingAutoSignIn = false },
                    onOpenAboutBert = { showAboutBertDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        } else {
            key(activeUser.uid) {
                val novaDeckViewModel: NovaDeckViewModel = viewModel(
                    key = activeUser.uid,
                    factory = viewModelFactory {
                        initializer {
                            val app = checkNotNull(this[APPLICATION_KEY])
                            val db = FirebaseFirestore.getInstance(
                                app.getString(R.string.firestore_database_id)
                            )
                            NovaDeckViewModel(
                                prefsRepo = PreferencesRepository(app.applicationContext),
                                cloudRepo = NovaDeckCloudRepository(db),
                                currentUser = activeUser
                            )
                        }
                    }
                )
                NovaDeckApp(
                    viewModel = novaDeckViewModel,
                    onSignOut = {
                        signOutUser(credentialManager, scope)
                    },
                    onOpenAboutBert = { showAboutBertDialog = true }
                )
            }
        }

        if (showAboutBertDialog) {
            AboutBertDeveloperDialog(
                onDismiss = { showAboutBertDialog = false }
            )
        }
    }
}

@Composable
fun NovaDeckApp(
    viewModel: NovaDeckViewModel,
    onSignOut: () -> Unit,
    onOpenAboutBert: () -> Unit
) {
    val prefs by viewModel.prefsState.collectAsStateWithLifecycle()
    val decks by viewModel.decks.collectAsStateWithLifecycle()
    val activeDeck by viewModel.activeDeck.collectAsStateWithLifecycle()
    val topicInput by viewModel.slideTopicInput.collectAsStateWithLifecycle()
    val audienceInput by viewModel.slideAudienceInput.collectAsStateWithLifecycle()
    val slideCountInput by viewModel.slideCountInput.collectAsStateWithLifecycle()
    val selectedTheme by viewModel.slideThemeInput.collectAsStateWithLifecycle()
    val slideGenState by viewModel.slideGenState.collectAsStateWithLifecycle()
    val cloudError by viewModel.cloudError.collectAsStateWithLifecycle()

    val nicAiMode by viewModel.nicAiMode.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val nicAiUiState by viewModel.nicAiUiState.collectAsStateWithLifecycle()

    val gradeFilter by viewModel.gradeFilter.collectAsStateWithLifecycle()
    val subjectFilter by viewModel.subjectFilter.collectAsStateWithLifecycle()
    val difficultyFilter by viewModel.difficultyFilter.collectAsStateWithLifecycle()
    val selectedModuleId by viewModel.selectedModuleId.collectAsStateWithLifecycle()
    val walkthroughChecked by viewModel.walkthroughChecked.collectAsStateWithLifecycle()
    val miniCheckAnswers by viewModel.miniCheckAnswers.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_HOME

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTo(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag("nav_${item.route}")
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (currentRoute != ROUTE_NICAI) {
                    ExtendedFloatingActionButton(
                        onClick = { navigateTo(ROUTE_NICAI) },
                        icon = {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        },
                        text = { Text("Ask NicAI") },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.testTag("floating_nicai_fab")
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        header = {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "ND",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTo(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag("rail_${item.route}")
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = ROUTE_HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(ROUTE_HOME) {
                        HomeScreen(
                            prefs = prefs,
                            decks = decks,
                            modules = viewModel.allModules,
                            userEmail = viewModel.userEmail,
                            userDisplayName = viewModel.userDisplayName,
                            isSchoolWorkspace = viewModel.isSchoolWorkspace,
                            cloudError = cloudError,
                            onDismissCloudError = viewModel::dismissCloudError,
                            onRetryCloudSync = viewModel::retryCloudSync,
                            onSignOut = onSignOut,
                            onNavigateToSlides = { navigateTo(ROUTE_SLIDES) },
                            onOpenDeck = { deckId ->
                                viewModel.selectDeck(deckId)
                                navigateTo(ROUTE_SLIDES)
                            },
                            onNavigateToNicAi = { navigateTo(ROUTE_NICAI) },
                            onNavigateToStudyHub = { navigateTo(ROUTE_STUDY) },
                            onOpenModule = { moduleId ->
                                viewModel.openModule(moduleId)
                                navigateTo(ROUTE_STUDY)
                            },
                            onNavigateToPlans = { navigateTo(ROUTE_PLANS) },
                            onToggleTheme = { dark -> viewModel.toggleDarkTheme(dark) },
                            onOpenAboutBert = onOpenAboutBert
                        )
                    }

                    composable(ROUTE_SLIDES) {
                        SlideStudioScreen(
                            prefs = prefs,
                            decks = decks,
                            activeDeck = activeDeck,
                            topicInput = topicInput,
                            audienceInput = audienceInput,
                            slideCountInput = slideCountInput,
                            selectedTheme = selectedTheme,
                            genState = slideGenState,
                            onTopicChange = viewModel::updateSlideTopicInput,
                            onAudienceChange = viewModel::updateSlideAudienceInput,
                            onSlideCountChange = viewModel::updateSlideCountInput,
                            onSelectDeck = viewModel::selectDeck,
                            onSwitchTheme = viewModel::switchDeckTheme,
                            onGenerateDeck = viewModel::generateSlideDeck,
                            onClearGenState = viewModel::clearSlideGenState,
                            onUpdateSlide = viewModel::updateSlide,
                            onAddSlide = viewModel::addQuickSlide,
                            onDeleteSlide = viewModel::deleteSlide,
                            onNavigateToPlans = { navigateTo(ROUTE_PLANS) },
                            onShowMessage = ::showMessage
                        )
                    }

                    composable(ROUTE_NICAI) {
                        NicAiScreen(
                            prefs = prefs,
                            activeMode = nicAiMode,
                            messages = chatMessages,
                            uiState = nicAiUiState,
                            onSelectMode = viewModel::setNicAiMode,
                            onSendMessage = viewModel::sendNicAiMessage,
                            onDismissError = viewModel::clearNicAiError,
                            onTurnIntoSlides = { topic ->
                                viewModel.updateSlideTopicInput(topic)
                                navigateTo(ROUTE_SLIDES)
                            },
                            onNavigateToPlans = { navigateTo(ROUTE_PLANS) }
                        )
                    }

                    composable(ROUTE_STUDY) {
                        StudyHubScreen(
                            prefs = prefs,
                            modules = viewModel.allModules,
                            gradeFilter = gradeFilter,
                            subjectFilter = subjectFilter,
                            difficultyFilter = difficultyFilter,
                            selectedModuleId = selectedModuleId,
                            walkthroughChecked = walkthroughChecked,
                            miniCheckAnswers = miniCheckAnswers,
                            onGradeFilterChange = viewModel::setGradeFilter,
                            onSubjectFilterChange = viewModel::setSubjectFilter,
                            onDifficultyFilterChange = viewModel::setDifficultyFilter,
                            onOpenModule = viewModel::openModule,
                            onCloseModule = viewModel::closeModule,
                            onToggleWalkthroughStep = viewModel::toggleWalkthroughStep,
                            onAnswerMiniCheck = viewModel::answerMiniCheck,
                            onMarkModuleDone = viewModel::markModuleDone,
                            onAskNicAiAboutTopic = { prompt ->
                                navigateTo(ROUTE_NICAI)
                                viewModel.sendNicAiMessage(prompt)
                            },
                            onTurnModuleIntoSlides = { topic ->
                                viewModel.updateSlideTopicInput(topic)
                                navigateTo(ROUTE_SLIDES)
                            },
                            onShowMessage = ::showMessage
                        )
                    }

                    composable(ROUTE_PLANS) {
                        PlansScreen(
                            prefs = prefs,
                            userEmail = viewModel.userEmail,
                            isSchoolWorkspace = viewModel.isSchoolWorkspace,
                            onToggleProDemo = viewModel::toggleProDemo,
                            onOpenAboutBert = onOpenAboutBert,
                            onSignOut = onSignOut
                        )
                    }
                }
            }
        }
    }
}
