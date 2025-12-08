# Car Rental Management System

## 📁 Project Structure
- `src/` - Java source code
- `lib/` - SQLite JDBC driver (included)
- `reports/` - Project documentation
- `car_rental.db` - SQLite database file
- `nbproject/` - NetBeans project configuration

## 🚀 How to Run
1. **Download:** `git clone https://github.com/imgettingcookedman/CarRentalSystem.git`
   Or download ZIP from GitHub (Code → Download ZIP)
2. **Extract** ZIP file (if downloaded)
3. **Open in NetBeans:** File → Open Project → select project folder
4. **Run:** Click the green Run button ▶️

## 📋 Requirements
- Java JDK 17 or higher
- NetBeans IDE (recommended) or any Java IDE

## ⚠️ Troubleshooting Common Issues

### **1. Database Driver Error:**

**Solution:**
1. In NetBeans: **Right-click project → Properties → Libraries**
2. **Compile Tab:**
   - If you see a RED/BROKEN reference to `sqlite-jdbc-3.36.0.3.jar`:
     - Select it → Click **Remove**
   - Click **"Add JAR/Folder"**
   - Navigate to the project's `lib/` folder
   - Select `sqlite-jdbc-3.36.0.3.jar`
   - Make sure ✅ checkbox is **CHECKED** (Build Dependencies)
3. **Run Tab:**
   - Add the same JAR file again
   - Make sure it appears in the list
4. Click **OK** and run again

