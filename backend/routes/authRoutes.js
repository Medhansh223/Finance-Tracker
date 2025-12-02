const express = require("express");
const jwt = require("jsonwebtoken");
const User = require("../models/User");

const router = express.Router();

// helper to create tokens
const createAccessToken = (userId) =>
  jwt.sign(
    { 
        id: userId 
    }, 
    process.env.ACCESS_TOKEN_SECRET, 
    {
        expiresIn: process.env.ACCESS_TOKEN_EXPIRY,
    }
);

const createRefreshToken = (userId) =>
  jwt.sign(
    { 
        id: userId 
    }, 
    process.env.REFRESH_TOKEN_SECRET, 
    {
        expiresIn: process.env.REFRESH_TOKEN_EXPIRY,  
    }
);

// SIGNUP
router.post("/signup", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    const existing = await User.findOne({ email });
    if (existing) return res.status(400).json({ message: "User already exists" });

    const user = await User.create({ name, email, password });

    res.status(201).json({ message: "User registered" });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server error" });
  }
});

// LOGIN
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user || !(await user.matchPassword(password)))
      return res.status(400).json({ message: "Invalid credentials" });

    const accessToken = createAccessToken(user._id);
    const refreshToken = createRefreshToken(user._id);

    user.refreshToken = refreshToken;
    await user.save();

    // httpOnly cookie for refresh token
    res.cookie("jwt", refreshToken, {
      httpOnly: true,
      secure: true,        // true in production with https
      sameSite: "strict",
      maxAge: 7 * 24 * 60 * 60 * 1000,
    });

    res.json({
      accessToken,
      user: { id: user._id, name: user.name, email: user.email },
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server error" });
  }
});

// REFRESH TOKEN – get new access token
router.get("/refresh", async (req, res) => {
  try {
    const token = req.cookies?.jwt;
    if (!token) return res.status(401).json({ message: "No refresh token" });

    const decoded = jwt.verify(token, process.env.REFRESH_TOKEN_SECRET);

    const user = await User.findById(decoded.id);
    if (!user || user.refreshToken !== token)
      return res.status(403).json({ message: "Invalid refresh token" });

    const accessToken = createAccessToken(user._id);
    res.json({ accessToken });
  } catch (err) {
    console.error(err);
    res.status(403).json({ message: "Refresh token invalid/expired" });
  }
});

// LOGOUT
router.post("/logout", async (req, res) => {
  try {
    const token = req.cookies?.jwt;
    if (token) {
      const decoded = jwt.decode(token);
      if (decoded?.id) {
        const user = await User.findById(decoded.id);
        if (user) {
          user.refreshToken = null;
          await user.save();
        }
      }
    }
    res.clearCookie("jwt");
    res.json({ message: "Logged out" });
  } catch {
    res.clearCookie("jwt");
    res.json({ message: "Logged out" });
  }
});

module.exports = router;
