import type { TransactionType } from "./auth";

export interface Transaction {
  id: number;
  amount: number;
  type: TransactionType;
  category: string;
  description: string | null;
  date: string;
}

export interface TransactionRequest {
  amount: number;
  type: TransactionType;
  category: string;
  description?: string;
  date: string;
}
