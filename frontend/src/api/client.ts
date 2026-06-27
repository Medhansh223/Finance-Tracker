import { API_URL } from "../config/env";
import type { ApiErrorResponse } from "../types/auth";
import {
  clearSessionToken,
  getSessionToken,
} from "../utils/session";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

async function parseErrorMessage(response: Response): Promise<string> {
  try {
    const data = (await response.json()) as ApiErrorResponse;
    return data.message || "Request failed";
  } catch {
    return "Request failed";
  }
}

export async function apiFetch(
  path: string,
  options: RequestInit = {},
  authenticated = false
): Promise<Response> {
  const headers = new Headers(options.headers);
  headers.set("Content-Type", "application/json");

  if (authenticated) {
    const sessionToken = getSessionToken();
    if (!sessionToken) {
      throw new ApiError("Session expired. Please login again.", 401);
    }
    headers.set("Authorization", `Bearer ${sessionToken}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  if (authenticated && response.status === 401) {
    clearSessionToken();
    throw new ApiError("Session expired. Please login again.", 401);
  }

  if (!response.ok) {
    const message = await parseErrorMessage(response);
    throw new ApiError(message, response.status);
  }

  return response;
}
