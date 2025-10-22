#!/bin/bash

# Install pre-commit hooks
pip install pre-commit
pre-commit install

# Install pre-commit hook for API key protection
cp scripts/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# Install commit-msg hook for gitflow enforcement
cp scripts/commit-msg .git/hooks/commit-msg
chmod +x .git/hooks/commit-msg

# Install pre-push hook
cp scripts/pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push

# Configure git for gitflow
git config gitflow.branch.master main
git config gitflow.branch.develop develop
git config gitflow.prefix.feature feature/
git config gitflow.prefix.hotfix hotfix/
git config gitflow.prefix.release release/

echo "✅ Git hooks and gitflow configuration installed successfully"
echo "📋 Use these branch patterns:"
echo "  - feature/your-feature-name"
echo "  - hotfix/your-hotfix-name"
echo "  - release/version-number"