var gulp = require('gulp');
var cache = require('gulp-cached');
var eslint = require('gulp-eslint');
var sass = require('gulp-sass');
var sourcemaps = require('gulp-sourcemaps');
var autoprefixer = require('gulp-autoprefixer');
var notify = require('gulp-notify');
var browserSync = require('browser-sync').create();


var scss = {
	files : "./styles/**/*.scss",
	output: "./resources/css",
	options: {
		errLogToConsole: true,
		outputStyle: "expanded"
	}
};

var javascript = {
	files: "./resources/js/**/*.js"
};

var autoprefixerOptions = {
	browsers: ['last 2 versions', '> 5%', 'Firefox ESR']
};

gulp.task('lint', function () {
	return gulp
		.src(javascript.files)
		.pipe(cache('linting'))
		.pipe(eslint())
		.pipe(eslint.format())
		.pipe(eslint.failOnError())
		.pipe(notify({message: "Linting complete"}));
});

gulp.task('sass', function () {
	return gulp
		.src(scss.files)
		.pipe(cache('scss'))
		.pipe(sass(scss.options).on("error", sass.logError))
		.pipe(autoprefixer(autoprefixerOptions))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest(scss.output))
		.pipe(notify({message: "SCSS complete"}));
});

gulp.task('serve', function() {
	browserSync.init({
		proxy: "localhost:8080"
	}, function () {
		gulp.watch(scss.output + "/*", function () {
			gulp.src(scss.output + "/").pipe(browserSync.stream());
		});
	});
});

gulp.task('watch', function () {
	gulp.watch(scss.files, ['sass']);
	gulp.watch(javascript.files, ['lint']);
});

gulp.task('start', ['sass']);

gulp.task('default', ['serve', 'watch']);
