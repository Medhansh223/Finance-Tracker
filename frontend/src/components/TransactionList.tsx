import type { Transaction } from "../types/transaction";

interface TransactionListProps {
  transactions: Transaction[];
  loading: boolean;
}

export default function TransactionList({
  transactions,
  loading,
}: TransactionListProps) {
  return (
    <div className="card">
      <h3>Your Transactions</h3>

      {loading ? (
        <p>Loading transactions...</p>
      ) : transactions.length === 0 ? (
        <p>No transactions yet.</p>
      ) : (
        <div className="transactions">
          {transactions.map((transaction) => (
            <div key={transaction.id}>
              <b>{transaction.type.toUpperCase()}</b> - ₹{transaction.amount}
              <br />
              Category: {transaction.category}
              <br />
              {transaction.description ?? ""}
              <br />
              <small>
                {new Date(transaction.date).toLocaleDateString()}
              </small>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
