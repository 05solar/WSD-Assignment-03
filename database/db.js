const { DataTypes } = require('sequelize');
const { sequelize } = require('../utils/database');

const Job = sequelize.define('Job', {
    job_id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    title: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    company: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    location: {
        type: DataTypes.STRING,
    },
    link: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
    },
    experience: {
        type: DataTypes.STRING,
        defaultValue: 'unknown',
    },
    education: {
        type: DataTypes.STRING,
        defaultValue: 'No education provided',
    },
    employmentType: {
        type: DataTypes.STRING,
        defaultValue: 'No employment type provided',
    },
    deadline: {
        type: DataTypes.STRING,
        defaultValue: 'No deadline provided',
    },
    sector: {
        type: DataTypes.STRING,
        defaultValue: 'No sector provided',
    },
    postedDate: {
        type: DataTypes.DATE,
        allowNull: false,
    },
    views: {
        type: DataTypes.INTEGER,
        defaultValue: 0,
    },
}, {
    timestamps: true,
});

module.exports = Job;
