module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);

  var appPath = require('./bower.json').appPath;
  grunt.initConfig({
    paths  : {
      html: appPath + '/pages',
      scss: appPath + '/styles',
      css : appPath + '/resources/css',
      js  : appPath + '/resources/js'
    },
    autoprefixer: {
      dev: {
        options: {
          map: true
        },
        multiple_files: {
          expand: true,
          flatten: true,
          src: '<%= paths.css %>/*.css',
          dest: '<%= paths.css %>/'
        }
      },
      dist: {
        options: {
          browsers: ['last 2 versions', 'ie 9']
        },
        multiple_files: {
          expand: true,
          flatten: true,
          src: '<%= paths.css %>/*.css',
          dest: '<%= paths.css %>/'
        }
      }
    },
    compass: {
      dev: {
        options: {
          sassDir: '<%= paths.scss %>',
          cssDir : '<%= paths.css %>'
        }
      }
    },
    cssmin: {
      target: {
        files: [{
          expand: true,
          cwd: '<%= paths.css %>',
          src: ['*/*.css', '!*.min.css'],
          dest: '<%= paths.css %>',
          ext: '.css'
        }]
      }
    },
    jshint : {
      options: {
        reporter  : require('jshint-stylish'),
        'jshintrc': appPath + '/.jshintrc'
      },
      target : ['<%= paths.js %>/**/*.js', '!<%= paths.js %>/**/*-min.js', '!<%= paths.js %>/vendor/**/*.js']
    },
    watch  : {
      compass: {
        files  : ['<%= paths.scss %>/**/*.scss'],
        tasks  : ['compass:dev'],
        options: {
          livereload: true
        }
      },
      html   : {
        files  : ['<%= paths.html %>/**/*.html'],
        options: {
          livereload: true
        }
      },
      jshint : {
        files  : ['<%= paths.js %>/**/*.js'],
        tasks  : ['newer:jshint'],
        options: {
          livereload: true
        }
      }
    }
  });

  grunt.registerTask("default", []);
  grunt.registerTask("dev", ['compass:dev', 'autoprefixer']);
  grunt.registerTask("dist", ['compass:dev', 'autoprefixer', 'cssmin']);
};