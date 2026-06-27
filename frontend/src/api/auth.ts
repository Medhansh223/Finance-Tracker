import { apiFetch } from "./client";
import type { AuthResponse, MessageResponse } from "../types/auth";
import { clearSessionToken, setSessionToken } from "../utils/session";

interface LoginRequest {
  email: string;
  password: string;
}

interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export async function login(request: LoginRequest): Promise<void> {
  const response = await apiFetch("/auth/login", {
    method: "POST",
    body: JSON.stringify(request),
  });

  const data = (await response.json()) as AuthResponse;
  setSessionToken(data.sessionToken);
}

export async function signup(request: SignupRequest): Promise<MessageResponse> {
  const response = await apiFetch("/auth/signup", {
    method: "POST",
    body: JSON.stringify(request),
  });

  return response.json() as Promise<MessageResponse>;
}

export async function logout(): Promise<void> {
  try {
    await apiFetch("/auth/logout", { method: "POST" }, true);
  } finally {
    clearSessionToken();
  }
}
