var gulp = require('gulp');
var cache = require('gulp-cached');
var sass = require('gulp-sass');
var sourcemaps = require('gulp-sourcemaps');
var autoprefixer = require('gulp-autoprefixer');

var scss = {
	files : "./styles/**/*.scss",
	output: "./resources/css",
	dev: {
		errLogToConsole: true,
		outputStyle: "expanded"
	},
	prod: {
		errLogToConsole: true,
		outputStyle: 'compressed'
	}
};

var autoprefixerOptions = {
	browsers: ['last 2 versions', '> 5%', 'Firefox ESR']
};

gulp.task('sass', function () {
	return gulp
		.src(scss.files)
		.pipe(cache('scss'))
		.pipe(sass(scss.dev).on("error", sass.logError))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest(scss.output));
});

gulp.task('sass:prod', function () {
	return gulp.src(scss.files)
		.pipe(sass(scss.prod).on("error", sass.logError))
		.pipe(autoprefixer(autoprefixerOptions))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest(scss.output));
});

gulp.task('watch', function() {
	gulp.watch(scss.files, ['sass']);
});

gulp.task('start', ['sass:prod']);

gulp.task('default', ['serve', 'watch']);
