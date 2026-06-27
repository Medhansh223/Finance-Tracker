import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { logout } from "../api/auth";
import { ApiError } from "../api/client";
import {
  createTransaction,
  getTransactions,
} from "../api/transactions";
import TransactionForm from "../components/TransactionForm";
import TransactionList from "../components/TransactionList";
import type { Transaction, TransactionRequest } from "../types/transaction";
import "../styles/dashboard.css";

export default function DashboardPage() {
  const navigate = useNavigate();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);

  const loadTransactions = useCallback(async () => {
    setLoading(true);

    try {
      const data = await getTransactions();
      setTransactions(data);
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        alert(error.message);
        navigate("/");
        return;
      }

      const message =
        error instanceof ApiError
          ? error.message
          : "Failed to load transactions";
      alert(message);
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    void loadTransactions();
  }, [loadTransactions]);

  async function handleLogout() {
    await logout();
    navigate("/");
  }

  async function handleAddTransaction(request: TransactionRequest) {
    try {
      await createTransaction(request);
      alert("Transaction Added!");
      await loadTransactions();
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        alert(error.message);
        navigate("/");
        return;
      }

      const message =
        error instanceof ApiError
          ? error.message
          : "Failed to add transaction";
      alert(message);
    }
  }

  return (
    <div className="container">
      <div className="header">
        <h2>Dashboard</h2>
        <button className="logout-btn" type="button" onClick={handleLogout}>
          Logout
        </button>
      </div>

      <TransactionForm onSubmit={handleAddTransaction} />
      <TransactionList transactions={transactions} loading={loading} />
    </div>
  );
}
