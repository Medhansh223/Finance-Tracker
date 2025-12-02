const express = require("express");
const Transaction = require("../models/Transaction");
const protect = require("../middleware/authMiddleware");

const router = express.Router();

// All routes require authentication
router.use(protect);

// CREATE TRANSACTION
router.post("/", async (req, res) => {
  try {
    const { amount, type, category, description, date } = req.body;

    const tx = await Transaction.create({
      user: req.user.id,       // ✔ FIXED
      amount,
      type,
      category,
      description,
      date
    });

    res.status(201).json(tx);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server error" });
  }
});

// GET ALL TRANSACTIONS
router.get("/", async (req, res) => {
  try {
    const txs = await Transaction.find({ user: req.user.id })
      .sort({ date: -1 });

    res.json(txs);
  } catch (err) {
    res.status(500).json({ message: "Server error" });
  }
});

// UPDATE
router.put("/:id", async (req, res) => {
  try {
    const tx = await Transaction.findOneAndUpdate(
      { _id: req.params.id, user: req.user.id },
      req.body,
      { new: true }
    );

    res.json(tx);
  } catch (err) {
    res.status(500).json({ message: "Server error" });
  }
});

// DELETE
router.delete("/:id", async (req, res) => {
  try {
    await Transaction.findOneAndDelete({ _id: req.params.id, user: req.user.id });
    res.json({ message: "Deleted" });
  } catch (err) {
    res.status(500).json({ message: "Server error" });
  }
});

module.exports = router;
