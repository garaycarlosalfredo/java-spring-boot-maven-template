#!/usr/bin/env bash
# PreToolUse guard. Reads the hook JSON from stdin; exit 2 blocks the tool call
# and sends stderr back to Claude.
#
# Rule (see AGENTS.md):
#   Never access the local .env (credentials). .env.example is allowed.
# Flyway migrations are intentionally not guarded: this is a template, so
# derived projects are expected to adapt them.

input=$(cat)
tool=$(jq -r '.tool_name // empty' <<<"$input")
path=$(jq -r '.tool_input.file_path // .tool_input.notebook_path // .tool_input.path // empty' <<<"$input")
cmd=$(jq -r '.tool_input.command // empty' <<<"$input")

block() {
  echo "Blocked by .claude/hooks/guard.sh: $1" >&2
  exit 2
}

# Check 1: file-based tools targeting .env or .env.* (except .env.example)
if [[ -n "$path" ]]; then
  base=$(basename "$path")
  if [[ "$base" == .env || ( "$base" == .env.* && "$base" != .env.example ) ]]; then
    block "'$base' holds local credentials and must not be read or modified. Use .env.example for placeholders."
  fi
fi

# Check 2: Bash commands that mention a .env file (not .env.example)
if [[ "$tool" == "Bash" && -n "$cmd" ]]; then
  if grep -qP '(^|[\s/"'"'"'=<>])\.env(\.(?!example\b)[A-Za-z0-9_.-]+)?($|[\s"'"'"';|&<>)])' <<<"$cmd"; then
    block "this command references a local .env file. Do not read, print, or copy credentials."
  fi
fi

exit 0
