import { useState, type FormEvent } from "react";
import type { TransactionType } from "../types/auth";
import type { TransactionRequest } from "../types/transaction";

interface TransactionFormProps {
  onSubmit: (request: TransactionRequest) => Promise<void>;
}

const emptyForm: TransactionRequest = {
  amount: 0,
  type: "expense",
  category: "",
  description: "",
  date: new Date().toISOString().slice(0, 10),
};

export default function TransactionForm({ onSubmit }: TransactionFormProps) {
  const [form, setForm] = useState<TransactionRequest>(emptyForm);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);

    try {
      await onSubmit({
        ...form,
        amount: Number(form.amount),
      });
      setForm({
        ...emptyForm,
        date: new Date().toISOString().slice(0, 10),
      });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h3>Add Transaction</h3>

      <div className="form-row">
        <input
          type="number"
          placeholder="Amount"
          value={form.amount || ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              amount: Number(event.target.value),
            }))
          }
          min="0.01"
          step="0.01"
          required
        />
        <select
          value={form.type}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              type: event.target.value as TransactionType,
            }))
          }
        >
          <option value="income">Income</option>
          <option value="expense">Expense</option>
        </select>
      </div>

      <div className="form-row">
        <input
          type="text"
          placeholder="Category"
          value={form.category}
          onChange={(event) =>
            setForm((current) => ({ ...current, category: event.target.value }))
          }
          required
        />
        <input
          type="text"
          placeholder="Description"
          value={form.description ?? ""}
          onChange={(event) =>
            setForm((current) => ({
              ...current,
              description: event.target.value,
            }))
          }
        />
      </div>

      <div className="form-row">
        <input
          type="date"
          value={form.date}
          onChange={(event) =>
            setForm((current) => ({ ...current, date: event.target.value }))
          }
          required
        />
        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? "Adding..." : "Add"}
        </button>
      </div>
    </form>
  );
}
