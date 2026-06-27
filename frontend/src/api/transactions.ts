import { apiFetch } from "./client";
import type { Transaction, TransactionRequest } from "../types/transaction";

export async function getTransactions(): Promise<Transaction[]> {
  const response = await apiFetch("/transactions", {}, true);
  return response.json() as Promise<Transaction[]>;
}

export async function createTransaction(
  request: TransactionRequest
): Promise<Transaction> {
  const response = await apiFetch(
    "/transactions",
    {
      method: "POST",
      body: JSON.stringify(request),
    },
    true
  );

  return response.json() as Promise<Transaction>;
}
