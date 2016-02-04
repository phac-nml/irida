var gulp = require('gulp');
var eslint = require('gulp-eslint');
var sass = require('gulp-sass');
var sourcemaps = require('gulp-sourcemaps');
var autoprefixer = require('gulp-autoprefixer');


var scss = {
	files : "./styles/**/*.scss",
	output: "./resources/css",
	options: {
		errLogToConsole: true,
		outputStyle: "expanded"
	}
};

var autoprefixerOptions = {
	browsers: ['last 2 versions', '> 5%', 'Firefox ESR']
};

gulp.task('lint', function () {
	return gulp
		.src("./resources/js/**")
		.pipe(eslint())
		.pipe(eslint.format())
		.pipe(eslint.failOnError());
});

gulp.task('sass', function () {
	return gulp
		.src(scss.files)
		.pipe(sass(scss.options).on("error", sass.logError))
		.pipe(sourcemaps.write())
		.pipe(autoprefixer(autoprefixerOptions))
		.pipe(gulp.dest(scss.output));
});

gulp.task('watch', function () {
	gulp.watch(scss.files, ['sass']);
});

gulp.task('start', ['sass'], function () {

});

gulp.task('default', ['lint', 'sass'], function () {

});
