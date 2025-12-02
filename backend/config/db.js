const mongoose =  require("mongoose");

const connectDB = async() => {
    try {
        await mongoose.connect(process.env.MONGO_URI)
        console.log("MONGODB CONNECTED")
    } catch (error) {
        console.log("MONGODB NOT CONNECTED",error);
        process.exit(1)
    }
};

module.exports = connectDB;