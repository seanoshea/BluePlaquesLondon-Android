# Contributing to Blue Plaques London (Android)

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Create a feature branch** from `develop`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Read the [DEVELOPMENT.md](../BluePlaquesLondon/DEVELOPMENT.md)** for setup instructions

## Development Workflow

### Before You Start

- Check existing issues and PRs to avoid duplicate work
- Discuss major changes in an issue first
- Ensure you're working off the `develop` branch

### Making Changes

1. **Follow code conventions**:
   - Use consistent naming (camelCase for variables/methods, PascalCase for classes)
   - Keep lines reasonably short (max ~120 characters)
   - Add comments for non-obvious logic
   - Use meaningful variable and function names

2. **Write tests**:
   - Add unit tests for new business logic
   - Aim for test coverage above 80% for new code
   - Use JUnit 4 and Mockito for unit tests
   - Place tests in `src/test/java/` with matching package structure

3. **Follow the project architecture**:
   - Use MVVM pattern with ViewModels and Repository
   - Inject dependencies with Hilt
   - Keep UI logic in ViewModels, not Views
   - Use LiveData for state management

### Testing

Run tests before committing:

```bash
./gradlew testDebugUnitTest
```

For UI changes, also run:

```bash
./gradlew connectedDebugAndroidTest
```

Check code quality:

```bash
./gradlew lint
```

## Submitting a Pull Request

### PR Guidelines

1. **Clear title**: Briefly describe what your PR does
   - ✅ "Add search filtering for plaque occupation"
   - ❌ "Fix stuff"

2. **Descriptive description**: Include:
   - What problem does this solve?
   - How does it solve it?
   - Any breaking changes?
   - Screenshots for UI changes

3. **One feature per PR**: Keep changes focused and reviewable

4. **Keep PRs up to date**: Rebase on `develop` if it diverges

### PR Checklist

Before submitting, ensure:

- [ ] Tests pass: `./gradlew testDebugUnitTest`
- [ ] Code compiles: `./gradlew assembleDebug`
- [ ] Lint passes: `./gradlew lint`
- [ ] No hardcoded strings (use `strings.xml`)
- [ ] No API keys or secrets in code
- [ ] Updated documentation if adding features
- [ ] Added/updated tests for new code
- [ ] Commit messages are clear and descriptive

### Commit Message Guidelines

Write clear, descriptive commit messages:

```
Fix memory leak in MapDetailViewModel

- Remove listener registration in onCleared()
- Add lifecycle management for RxJava subscriptions
- Add unit test for lifecycle cleanup
```

**Good practices:**
- Use imperative mood ("Add feature", not "Added feature")
- First line is a summary (50 chars or less)
- Leave blank line, then add detailed explanation
- Reference issues: "Fixes #123"

## Code Style

### Java Conventions

```java
// Use this style for methods
private void updatePlaque(Placemark plaque) {
    // Implementation
}

// Use try-with-resources when possible
try (Cursor cursor = database.query(...)) {
    // Use cursor
}

// Prefer enhanced for loops
for (Placemark plaque : placemarks) {
    // Process plaque
}
```

### Resource Naming

- **Layouts**: `fragment_map_detail.xml`, `activity_main.xml`
- **Strings**: Descriptive keys in lowercase with underscores
  ```xml
  <string name="map_detail_title">Plaque Details</string>
  ```
- **Drawables**: Prefix with type: `ic_marker.xml`, `btn_search.xml`

### ViewModel Example

```java
@HiltViewModel
public class MapDetailViewModel extends ViewModel {
    private final PlaquesRepository repository;
    private final MutableLiveData<Placemark> plaque = new MutableLiveData<>();
    public LiveData<Placemark> getPlaque() {
        return plaque;
    }

    @Inject
    public MapDetailViewModel(PlaquesRepository repository) {
        this.repository = repository;
    }

    public void loadPlaque(String id) {
        repository.getPlaqueById(id)
            .subscribe(
                this.plaque::setValue,
                error -> Log.e(TAG, "Error loading plaque", error)
            );
    }
}
```

## Documentation

- Update README.md for user-facing features
- Update DEVELOPMENT.md for developer-facing changes
- Add inline comments for complex logic
- Update CHANGELOG.md when merging PRs

## Reporting Issues

### Before Reporting

- Check if the issue already exists
- Try reproducing on latest `develop` branch
- Gather relevant information (Android version, device, etc.)

### Issue Template

Include:

- **Description**: What's the problem?
- **Steps to reproduce**: How to trigger the issue?
- **Expected behavior**: What should happen?
- **Actual behavior**: What actually happens?
- **Environment**: Android version, device, app version
- **Screenshots/logs**: Visual evidence if applicable

## Types of Contributions

### Bug Fixes
- Clearly describe the bug
- Add tests that verify the fix
- Reference the issue being fixed

### Features
- Discuss major features in an issue first
- Follow the existing architecture
- Add comprehensive tests
- Update documentation

### Tests & Documentation
- Improving test coverage is always welcome
- Documentation improvements help everyone

### Performance
- Include benchmarks or profiling data
- Explain the performance impact

## Code Review Process

1. **Automated checks**: GitHub Actions runs tests and linting
2. **Code review**: Maintainers review for:
   - Code quality and style
   - Architecture compliance
   - Test coverage
   - Documentation
3. **Approval**: PRs require at least one approval
4. **Merge**: Squash merge to `develop` branch

## Community

- Be respectful and constructive
- Assume good intentions
- Help others learn and improve
- Celebrate contributions!

## Questions?

- Open an issue to discuss
- Review [DEVELOPMENT.md](../BluePlaquesLondon/DEVELOPMENT.md) for setup help
- Check existing PRs for similar work

## License

By contributing to this project, you agree that your contributions will be licensed under its BSD 2-Clause License.

---

Thank you for contributing to Blue Plaques London!
