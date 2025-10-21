# Claude AI Development Guidelines

## Android Development Optimization

### Core Principles
- **Jetpack Compose**: Prefer Compose over XML layouts
- **ViewModels**: Use ViewModels with StateFlow/LiveData
- **Dependency Injection**: Use Hilt for DI
- **Coroutines**: Use coroutines over AsyncTask/ExecutorService
- **Material 3**: Follow Material Design 3 guidelines

### Modern Patterns
```kotlin
// Compose with ViewModel
@Composable
fun FeatureScreen(viewModel: FeatureViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> CircularProgressIndicator()
        else -> LazyColumn {
            items(uiState.items) { item ->
                Text(text = item.name)
            }
        }
    }
}

@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
}
```

### Project Structure
```
app/src/main/java/
├── data/           # Repositories, data sources
├── domain/         # Use cases, models
├── presentation/   # UI, ViewModels
└── di/            # Dependency injection
```

## Token Usage Optimization

### Response Guidelines
- **Minimal Code**: Show only essential code changes
- **No Explanations**: Skip verbose descriptions unless critical
- **Direct Answers**: Provide solutions without preamble
- **Batch Operations**: Group multiple changes in single responses
- **Essential Only**: Exclude boilerplate, imports, or obvious code

### Request Format
```
// Good: Specific, minimal
"Add user service with CRUD operations"

// Bad: Verbose, unfocused  
"Can you please help me create a comprehensive user service that handles all CRUD operations with proper error handling and loading states?"
```

### Code Response Format
```kotlin
// Repository only - no imports/boilerplate
@Singleton
class UserRepository @Inject constructor(
    private val api: UserApi
) {
    suspend fun getUsers() = api.getUsers()
    suspend fun createUser(user: User) = api.createUser(user)
}
```

### Efficiency Rules
1. **One File Per Response**: Focus on single file changes
2. **Essential Methods Only**: Skip getters/setters unless needed
3. **No Type Definitions**: Assume interfaces exist unless creating new ones
4. **Minimal Templates**: Show only changed template sections
5. **Skip Tests**: Exclude unless specifically requested

### Android-Specific Optimizations
- Use `@Inject constructor()` without explanation
- Show class body only, skip annotations unless changed
- Prefer Compose functions over XML layouts
- Use data classes for models
- Skip lifecycle method explanations

### Communication Style
- Start with solution, not explanation
- Use bullet points for multiple items
- Avoid "Here's how to..." preambles
- End responses immediately after code/solution
- No closing pleasantries or offers for further help