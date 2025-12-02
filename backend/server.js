require("dotenv").config();
const express = require("express");
const cors = require("cors");
const connectDB = require("./config/db");
const cookieParser = require("cookie-parser");
const authRoutes = require("./routes/authRoutes");
const transactionRoutes = require("./routes/transactionRoutes");

const app = express();

app.use(express.json());
app.use(cookieParser());
app.use(cors({
  origin: "http://127.0.0.1:5500",
  credentials: true        // REQUIRED FOR COOKIES
}));

app.get("/", (req, res) => res.send("Backend running"));

app.use("/api/auth", authRoutes);
app.use("/api/transactions", transactionRoutes);

const PORT = process.env.PORT || 5000;
connectDB();

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));