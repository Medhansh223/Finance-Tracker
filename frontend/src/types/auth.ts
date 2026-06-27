export type TransactionType = "income" | "expense";

export interface AuthResponse {
  sessionToken: string;
}

export interface MessageResponse {
  message: string;
}

export interface ApiErrorResponse {
  message: string;
}
