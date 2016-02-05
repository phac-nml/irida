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
	dev: {
		errLogToConsole: true,
		outputStyle: "expanded"
	},
	prod: {
		errLogToConsole: true,
		outputStyle: 'compressed'
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
		.pipe(eslint.failOnError());
});

gulp.task('sass', function () {
	return gulp
		.src(scss.files)
		.pipe(cache('scss'))
		.pipe(sass(scss.dev).on("error", sass.logError))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest(scss.output))
		.pipe(notify({message: "SCSS complete"}));
});

gulp.task('sass:prod', function () {
	return gulp.src(scss.files)
		.pipe(sass(scss.prod).on("error", sass.logError))
		.pipe(autoprefixer(autoprefixerOptions))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest(scss.output));
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
	gulp.watch(javascript.files, ['lint']).on('change', browserSync.reload);
});

gulp.task('start', ['sass:prod']);

gulp.task('default', ['serve', 'watch']);
