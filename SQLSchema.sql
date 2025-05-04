-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    userId BIGINT NOT NULL AUTO_INCREMENT,
    institutionalId VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    userType VARCHAR(20) NOT NULL,
    PRIMARY KEY (userId)) AUTO_INCREMENT=1000
)

-- PRIORITY NUMBERS TABLE
CREATE TABLE IF NOT EXISTS priority_numbers(
    priorityNumber VARCHAR(20) NOT NULL,
    studentId VARCHAR(50) NOT NULL,
    tellerId VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (priorityNumber),
    INDEX idx_student (studentId)
)

-- TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS transactions (
    transactionId INT NOT NULL AUTO_INCREMENT,
    priorityNumber VARCHAR(20) NOT NULL,
    windowNumber INT NOT NULL,
    studentName VARCHAR(50) NOT NULL,
    studentId VARCHAR(50) NOT NULL,
    tellerId VARCHAR(50),
    amount DOUBLE DEFAULT 0.0,
    transactionType VARCHAR(30) NOT NULL,
    transactionDetails VARCHAR(255) NOT NULL,
    transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    calledTime TIMESTAMP NULL DEFAULT NULL,
    completionDate TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (transactionId),
    INDEX idx_student (studentId),
    INDEX idx_teller (tellerId),
    INDEX idx_status (status)
)

-- TELLER WINDOWS TABLE
CREATE TABLE IF NOT EXISTS teller_window (
    windowNumber INT NOT NULL,
    tellerId VARCHAR(50),
    studentId VARCHAR(50),
    priorityNumber VARCHAR(50),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastModified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (windowNumber),
    INDEX idx_teller (tellerId),
    INDEX idx_student (studentId)
)
